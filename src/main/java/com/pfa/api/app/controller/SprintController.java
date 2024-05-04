package com.pfa.api.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.SprintDTO;
import com.pfa.api.app.dto.responses.SprintResponse;
import com.pfa.api.app.service.SprintService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/sprints")
@RequiredArgsConstructor
public class SprintController {
    private final SprintService sprintService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    public ResponseEntity<JsonResponse> CreateSprint(@RequestBody SprintDTO entity) throws NotFoundException {
        SprintResponse Response = sprintService.AddSprint(entity);

        return new ResponseEntity<JsonResponse>(new JsonResponse(201, "Sprint has been Created successfully"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SprintResponse>> getSprints(@RequestParam Long projectId) throws NotFoundException {
        List<SprintResponse> Response = sprintService.getAllSprints(projectId);

        return ResponseEntity.ok(Response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponse> getSprintAndUserStories(@PathVariable Long id) throws NotFoundException {
        SprintResponse Response = sprintService.getSprint(id);
        return ResponseEntity.ok(Response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> adjustSprint(@PathVariable Long id, @RequestBody SprintDTO sprintDTO)
            throws NotFoundException {
        SprintResponse Response = sprintService.updateSprint(sprintDTO, id);

        return new ResponseEntity<JsonResponse>(new JsonResponse(202, "Sprint has been updated."), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteSprintById(@PathVariable Long id) throws NotFoundException {
        SprintResponse sprintResponse = sprintService.deleteSprint(id);

        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "Sprint has been Deleted"), HttpStatus.ACCEPTED);
    }

}
