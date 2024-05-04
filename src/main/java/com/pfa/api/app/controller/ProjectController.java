package com.pfa.api.app.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.ProjectDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.dto.responses.TeamPreferenceResponseDTO;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    @PostMapping
    public ResponseEntity<JsonResponse> createNewProject(@ModelAttribute ProjectDTO projectDTO,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "report", required = false) MultipartFile report)
            throws AccessDeniedException, NotFoundException {

        ProjectResponseDTO project = projectService.addProject(projectDTO, files, report);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(201, "Project has been created successfully!"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getProjects(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String academicYear) throws NotFoundException {
        List<ProjectResponseDTO> projects = projectService.getAllProjects(pageNumber, pageSize, academicYear);

        return ResponseEntity.ok(projects);
    }
    @GetMapping("/academicYear")
    public List<String>getAcademicYear(){
        List<String> academicYears = projectService.getAllAcademicYears();
        return academicYears;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) throws NotFoundException {
        ProjectResponseDTO project = projectService.getProject(id);

        return ResponseEntity.ok(project);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<JsonResponse> updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable long id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("report") MultipartFile report) throws NotFoundException {

        projectService.updateProject(projectDTO, id ,files,report);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(201, "Project has been Updated successfully!"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/docs/{docId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR','ROLE_RESPONSIBLE')")
    public ResponseEntity<ProjectResponseDTO> deleteFile(@PathVariable Long id, @PathVariable Long docId) throws NotFoundException, IOException {

        return new ResponseEntity<>(projectService.deleteFile(id, docId), HttpStatus.OK);
    }
    @GetMapping("/{projectId}/docs/{docId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long projectId, @PathVariable Long docId) throws NotFoundException, IOException {
        return projectService.downloadFile(projectId, docId);
    }



    @GetMapping("/accept/token")
    public ModelAndView acceptProjectWithToken(@RequestParam("token") String approvalToken) {
        projectService.validateToken(approvalToken);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("accept-project");
        modelAndView.setStatus(HttpStatus.OK);

        return modelAndView;

    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> acceptProject(@PathVariable Long id) throws NotFoundException {
        projectService.approveProject(id);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The project is accepted ,students can view and choose it now"), HttpStatus.OK);

    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> rejectProject(@PathVariable Long id) throws NotFoundException {
        projectService.rejectProject(id);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The project is rejected ,students can't view it"), HttpStatus.OK);

    }



    //endpoint for submitting Project preferences:
    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    @PostMapping("/preferences")
    public ResponseEntity<JsonResponse> submitProjectPreference(@RequestBody Map<Long, Integer> projectPreferences) throws NotFoundException {
        String message = projectService.submitProjectPreference(projectPreferences);
        return ResponseEntity.ok(new JsonResponse(200, message));
    }

    @GetMapping("/preferences")
    public ResponseEntity<List<TeamPreferenceResponseDTO>> getProjectPreference() throws NotFoundException {
        return new ResponseEntity<>(projectService.getAllProjectsPreferencesResponse(), HttpStatus.OK);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> assignUsersToProjects() throws NotFoundException{
        projectService.assignUsersToProjects();
       return new ResponseEntity<>(new JsonResponse(200, "the assignment presses is done , please review it and then validate it!"), HttpStatus.OK);
    }

    @PostMapping("/assign/validate")
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public  ResponseEntity<JsonResponse> validateAssignments() throws NotFoundException{
        projectService.validateAssignments();
        return new ResponseEntity<JsonResponse>(
            new JsonResponse(200, "projects are now assigned to teams"), HttpStatus.OK);
    }




}