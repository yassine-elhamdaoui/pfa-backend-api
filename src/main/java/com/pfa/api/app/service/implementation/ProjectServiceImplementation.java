package com.pfa.api.app.service.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.service.ProjectService;
import com.pfa.api.app.util.FileUtils;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImplementation implements ProjectService {

    @Value("${upload.directory}")
    public String DIRECTORY;
    private final Map<Long, List<TeamPreference>> userPreferences = new HashMap<>();//this one is for storing user preferences y by order in a HashMap

    private final EmailServiceImplementation emailService;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final ProjectPreferenceRepository projectPreferenceRepository;

    @SuppressWarnings("null")
    @Override
    public Project addProject(ProjectDTO projectDTO, List<MultipartFile> files)
            throws NotFoundException, AccessDeniedException {
        User owner = UserUtils.getCurrentUser(userRepository);
        Branch branch = branchRepository.findById(projectDTO.getBranch()).get();
        List<User> supervisors = new ArrayList<>();
        supervisors = findSupervisors(projectDTO.getSupervisors());
        supervisors.add(owner);

        Project project = Project.builder()
                .title(projectDTO.getTitle())
                .description(projectDTO.getDescription())
                .year(projectDTO.getYear())
                .techStack(projectDTO.getTechStack())
                .supervisors(supervisors)
                .codeLink(projectDTO.getCodeLink())
                .status(projectDTO.getStatus())
                .branch(branch)
                .approvalToken(UUID.randomUUID().toString())
                .isPublic(false)
                .build();

        Project savedProject = projectRepository.save(project);

        if (!files.isEmpty()) {
            FileUtils.saveDocuments(files, savedProject, DIRECTORY, documentRepository);
        }


        // email shit for the project approval
        // emailService.sendProjectApprovalEmail(savedProject);
        return savedProject;
    }

    @SuppressWarnings("null")
    @Override
    public Project getProject(Long id) throws NotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        return projectOptional.orElseThrow(() -> new NotFoundException());
    }

    @Override
    public List<Project> getAllProject() throws NotFoundException {
        User currentUser = UserUtils.getCurrentUser(userRepository);
        Boolean isHeadOfBranch = UserUtils.isHeadOfBranch(currentUser);
        return isHeadOfBranch == true ? projectRepository.findAll() : projectRepository.findByIsPublicTrue();
    }

    @SuppressWarnings("null")
    @Override
    public Project updateProject(ProjectDTO projectDTO, Long id) throws NotFoundException {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Project project = optionalProject.orElseThrow(NotFoundException::new);

        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }
        if (projectDTO.getTitle() != null) {
            project.setTitle(projectDTO.getTitle());
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
        if (projectDTO.getDueDates() != null) {
            project.setDueDates(projectDTO.getDueDates());
        }

        projectRepository.save(project);
        return project;

    }

    @SuppressWarnings("null")
    @Override
    public Project deleteFile(Long id, Long docId) throws NotFoundException {
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

        return projectRepository.save(project);
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

    ///
    @Override
    public String submitProjectPreference(Map<Long, Integer> projectPreferences) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(UserUtils.getCurrentUser(userRepository).getId());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        TeamPreference teamPreference = new TeamPreference();
        teamPreference.setUser(user);
        teamPreference.setProjectPreferenceRanks(projectPreferences);

        projectPreferenceRepository.save(teamPreference);

        //(#)this one will be replaced with Project theHead_of_branch will choose for thi group!
        //for now we return an mpty project till (#) will be done
    return "Team preferences submitted successfully !!";
    }
}


