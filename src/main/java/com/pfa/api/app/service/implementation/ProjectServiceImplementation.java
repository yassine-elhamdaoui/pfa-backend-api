package com.pfa.api.app.service.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.ProjectDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.dto.responses.TeamPreferenceResponseDTO;
import com.pfa.api.app.entity.Assignment;
import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.AssignmentRepository;
import com.pfa.api.app.repository.BranchRepository;
import com.pfa.api.app.repository.DocumentRepository;
import com.pfa.api.app.repository.ProjectPreferenceRepository;
import com.pfa.api.app.repository.ProjectRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.AssignmentService;
import com.pfa.api.app.service.ProjectService;
import com.pfa.api.app.util.FileUtils;
import com.pfa.api.app.util.ProjectUtils;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImplementation implements ProjectService {

    @Value("${upload.directory}")
    public String DIRECTORY;
    private final Map<Long, List<TeamPreference>> userPreferences;
    private final EmailServiceImplementation emailService;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final ProjectPreferenceRepository projectPreferenceRepository;
    private final AssignmentRepository assignmentRepository;
    private final TeamRepository teamRepository;
    private final AssignmentService assignmentService;

    @SuppressWarnings("null")
    @Override
    public ProjectResponseDTO addProject(ProjectDTO projectDTO, List<MultipartFile> files, MultipartFile report)
            throws NotFoundException, AccessDeniedException {
        User owner = UserUtils.getCurrentUser(userRepository);
        Branch branch = branchRepository.findById(projectDTO.getBranch()).get();
        List<User> supervisors = new ArrayList<>();
        supervisors = findSupervisors(projectDTO.getSupervisors());
        supervisors.add(owner);

        Project project = Project.builder()
                .title(projectDTO.getTitle())
                .description(projectDTO.getDescription())
                .techStack(projectDTO.getTechStack())
                .academicYear(projectDTO.getAcademicYear())
                .supervisors(supervisors)
                .codeLink(projectDTO.getCodeLink())
                .status(projectDTO.getStatus())
                .branch(branch)
                .approvalToken(UUID.randomUUID().toString())
                .isPublic(true)// false when i activate the email validation stuff
                .build();

        Project savedProject = projectRepository.save(project);

        if (files != null && !files.isEmpty()) {
            FileUtils.saveDocuments(files, savedProject, DIRECTORY, documentRepository);
        }
        if (report != null && !report.isEmpty()) {
            FileUtils.saveReport(report, savedProject, DIRECTORY, documentRepository);
            return ProjectResponseDTO.fromEntity(projectRepository.save(savedProject));
        }

        // email shit for the project approval
        // emailService.sendProjectApprovalEmail(savedProject);
        return ProjectResponseDTO.fromEntity(savedProject);
    }

    @SuppressWarnings("null")
    @Override
    public ProjectResponseDTO getProject(Long id) throws NotFoundException {
        User currentUser = UserUtils.getCurrentUser(userRepository);
        Boolean isHeadOfBranch = UserUtils.isHeadOfBranch(currentUser);
        Boolean isSupervisor = UserUtils.isSupervisor(currentUser);
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (!projectOptional.isPresent()) {
            throw new NotFoundException();
        }

        Project project = projectOptional.get();
        String[] years = project.getAcademicYear().split("/");
        int currentYear = Year.now().getValue();
        boolean isCurrentYearPresent = Arrays.asList(years).contains(String.valueOf(currentYear));

        if (!isCurrentYearPresent) {
            throw new NotFoundException();
        }

        if (isHeadOfBranch || (isSupervisor && project.getSupervisors().contains(currentUser))
                || project.getIsPublic()) {
            return ProjectResponseDTO.fromEntity(project);
        }

        throw new RuntimeException("Unauthorized access to project");
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects(int pageNumber, int pageSize, String academicYear)
            throws NotFoundException {
        User currentUser = UserUtils.getCurrentUser(userRepository);
        Boolean isHeadOfBranch = UserUtils.isHeadOfBranch(currentUser);
        Boolean isSupervisor = UserUtils.isSupervisor(currentUser);

        List<Project> projects;

        if (isHeadOfBranch) {
            projects = projectRepository.findByAcademicYear(academicYear);
        } else if (isSupervisor) {
            projects = projectRepository.findByAcademicYear(
                    academicYear).stream()
                    .filter(project -> project.getSupervisors().contains(currentUser))
                    .collect(Collectors.toList());
        } else {
            projects = projectRepository.findByAcademicYear(
                    academicYear).stream()
                    .filter(project -> project.getIsPublic() == true).collect(Collectors.toList());
        }

        // Paginate the projects list
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, projects.size());
        // 
        if (fromIndex>toIndex) {
            return new ArrayList<>();
        }
        // 
        List<Project> paginatedProjects = projects.subList(fromIndex, toIndex);

        return paginatedProjects.stream()
                .map(ProjectResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    public ProjectResponseDTO updateProject(ProjectDTO projectDTO, Long id, List<MultipartFile> files,
            MultipartFile report) throws NotFoundException {
        Project project = projectRepository.findById(id).orElseThrow(NotFoundException::new);

        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }
        if (projectDTO.getTitle() != null) {
            project.setTitle(projectDTO.getTitle());
        }
        if (projectDTO.getAcademicYear() != null) {
            project.setAcademicYear(projectDTO.getAcademicYear());
        }
        if (projectDTO.getCodeLink() != null) {
            project.setCodeLink(projectDTO.getCodeLink());
        }
        if (projectDTO.getStatus() != null) {
            project.setStatus(projectDTO.getTitle());
        }
        if (projectDTO.getTechStack() != null) {
            project.setTechStack(projectDTO.getTitle());
        }
        if (!files.isEmpty()) {
            FileUtils.saveDocuments(files, project, DIRECTORY, documentRepository);
        }
        if (!report.isEmpty()) {
            if (project.getReport() == null) {
                FileUtils.saveReport(report, project, DIRECTORY, documentRepository);

            } else {
                return deleteFile(id, project.getReport().getId());
            }
        }

        projectRepository.save(project);
        return ProjectResponseDTO.fromEntity(project);

    }

    @SuppressWarnings("null")
    @Override
    public ProjectResponseDTO deleteFile(Long id, Long docId) throws NotFoundException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());

        Optional<Document> documentOptional = project.getDocuments().stream()
                .filter(document -> document.getId() == docId)
                .findFirst();

        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();
            try {
                Files.deleteIfExists(
                        Paths.get(DIRECTORY + "/" + project.getId() + "/" + document.getDocName() + ".gz"));
                documentRepository.delete(document);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + e.getMessage());
            }
            project.getDocuments().remove(document);
        } else {
            throw new NotFoundException();
        }

        return ProjectResponseDTO.fromEntity(projectRepository.save(project));
    }

    private List<User> findSupervisors(List<Long> supervisorIds) {
        return supervisorIds.stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(UserUtils::isSupervisor)
                .collect(Collectors.toList());
    }

    @Override
    public void validateToken(String approvalToken) {
        Optional<Project> optionalProject = projectRepository.findByApprovalToken(approvalToken);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setIsPublic(true);
            project.setApprovalToken("");
            projectRepository.save(project);

        }
    }

    @Override
    public void approveProject(Long id) throws NotFoundException {
        Project project = projectRepository.findById(id).orElseThrow(NotFoundException::new);
        project.setIsPublic(true);
        projectRepository.save(project);
    }

    @Override
    public void rejectProject(Long id) throws NotFoundException {
        Project project = projectRepository.findById(id).orElseThrow(NotFoundException::new);
        project.getSupervisors().clear();
        projectRepository.delete(project);
    }

    ///
    @Override
    public String submitProjectPreference(Map<Long, Integer> projectPreferences) throws NotFoundException {
        User user = UserUtils.getCurrentUser(userRepository);
        if (projectPreferenceRepository.findPreferenceByUser(user).isPresent()) {
            throw new RuntimeException("you already made your choice");
        }
        TeamPreference teamPreference = new TeamPreference();
        teamPreference.setUser(user);
        teamPreference.setProjectPreferenceRanks(projectPreferences);

        projectPreferenceRepository.save(teamPreference);

        return "Team preferences submitted successfully !!";
    }

    @Override
    public List<TeamPreference> getAllProjectsPreferences() {
        List<TeamPreference> projectsPreferences = projectPreferenceRepository.findAll();
        Set<Entry<Long, Integer>> sets = projectsPreferences.get(0).getProjectPreferenceRanks().entrySet();
        for (Entry<Long, Integer> entry : sets) {
            System.out.println(entry.getValue());
        }
        return projectPreferenceRepository.findAll();
    }

    @Override
    public List<TeamPreferenceResponseDTO> getAllProjectsPreferencesResponse() {
        List<TeamPreference> projectsPreferences = projectPreferenceRepository.findAll();
        return projectsPreferences.stream()
                .map(TeamPreferenceResponseDTO::fromEntity)
                .collect(Collectors.toList());

    }

    @Override
    public Map<User, Project> assignUsersToProjects() throws NotFoundException {
        Map<User, Project> result = ProjectUtils.assignTeamsToProjects(getAllProjectsPreferences(), userRepository,
                projectPreferenceRepository, projectRepository);

        Assignment assignment = Assignment.builder()
                .branch(UserUtils.getCurrentUser(userRepository).getBranch())
                .date(new Date())
                .completed(false)
                .initiated(true)
                .build();
        assignmentRepository.save(assignment);
        return result;

    }

    @Override
    public void validateAssignments() throws NotFoundException {
        List<TeamPreference> allPreferences = getAllProjectsPreferences();
        Map<User, Project> assignedProjects = ProjectUtils.assignTeamsToProjects(getAllProjectsPreferences(),
                userRepository, projectPreferenceRepository, projectRepository);
        List<Project> projects = new ArrayList<>();
        List<User> responsibleUsers = new ArrayList<>();
        List<Team> teams = new ArrayList<>();

        for (Entry<User, Project> assignment : assignedProjects.entrySet()) {
            Project project = assignment.getValue();
            User user = assignment.getKey();
            Team team = user.getTeam();

            project.setTeam(user.getTeam());
            user.getTeam().setProject(project);
            team.setProject(project);

            projects.add(project);
            responsibleUsers.add(user);
            teams.add(team);
            // Send email to the members
            // for (User member : user.getTeam().getMembers()) {
            // String userMessage = "Dear " + member.getFirstName() + ",\n\n";
            // userMessage += "Your team have been assigned to the project: " +
            // project.getTitle() +" .\n";
            // userMessage += "Please review the details and get started as soon as
            // possible.\n\n";
            // userMessage += "Best regards,\nThe Project Management Team";

            // // Send email to the user
            // emailService.sendInformingEmail(member, userMessage);
            // }

            // Send email to the supervisors
            // for (User supervisor : project.getSupervisors()) {
            // String supervisorMessage = "Dear " + supervisor.getFirstName() + ",\n\n";
            // supervisorMessage += user.getTeam().getName() + " has been assigned to the
            // project: " + project.getTitle()
            // + ".\n";
            // supervisorMessage += "Please ensure proper guidance and support for the
            // assigned project.\n\n";
            // supervisorMessage += "Best regards,\nThe Project Management Team";
            // emailService.sendInformingEmail(supervisor, supervisorMessage);
            // }
        }

        // Save projects and users
        projectRepository.saveAll(projects);
        userRepository.saveAll(responsibleUsers);
        teamRepository.saveAll(teams);
        assignmentService.completeAssignment();

    }

    @Override
    public ResponseEntity<byte[]> downloadFile(Long projectId, Long docId) {
        Project project = projectRepository.findById(projectId).get();
        Document document = documentRepository.findById(docId).get();
        return FileUtils.downloadFile(project, document, DIRECTORY);
    }

    @Override
    public List<String> getAllAcademicYears() {
        List<Project> projects;
        List<String> academicYears = new ArrayList();
        projects = projectRepository.findAll();
        for (Project project : projects) {
            academicYears.add(project.getAcademicYear());
        }
        Set<String> uniqueYears = new HashSet<>(academicYears);
        List<String> uniqueYearsList =new ArrayList<>(uniqueYears);
        Collections.sort(uniqueYearsList,Collections.reverseOrder());
        
        return new ArrayList<>(uniqueYearsList);
       
       
    }

}
