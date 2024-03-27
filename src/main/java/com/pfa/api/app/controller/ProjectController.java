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
import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Project;
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
                                                         @RequestParam("files") List<MultipartFile> files) throws AccessDeniedException, NotFoundException {

        Project project = projectService.addProject(projectDTO, files);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(201, "Project has been created successfully!"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects() throws NotFoundException {
        List<Project> projects = projectService.getAllProject();

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) throws NotFoundException {
        Project project = projectService.getProject(id);

        return ResponseEntity.ok(project);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<JsonResponse> updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable long id) throws NotFoundException {

        projectService.updateProject(projectDTO, id);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(201, "Project has been Updated successfully!"), HttpStatus.OK);

    }

    @DeleteMapping("/{id}/docs/{docId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR','ROLE_RESPONSIBLE')")
    public ResponseEntity<Project> deleteFile(@PathVariable Long id, @PathVariable Long docId) throws NotFoundException, IOException {

        return new ResponseEntity<Project>(projectService.deleteFile(id, docId), HttpStatus.OK);
    }


    @GetMapping("/accept")
    public ModelAndView acceptProject(@RequestParam("token") String approvalToken) {
        projectService.validateToken(approvalToken);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("accept-project");
        modelAndView.setStatus(HttpStatus.OK);

        return modelAndView;

    }


    //endpoint for submitting Project preferences:
    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    @PostMapping("/preferences")
    public ResponseEntity<String> submitProjectPreference(@RequestBody Map<Long, Integer> projectPreferences) throws NotFoundException {
        String message = projectService.submitProjectPreference(projectPreferences);
        return ResponseEntity.ok(message);
    }
}