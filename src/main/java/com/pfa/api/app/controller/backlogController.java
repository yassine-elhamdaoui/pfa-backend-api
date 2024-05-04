package com.pfa.api.app.controller;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.responses.BacklogResponseDTO;
import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.service.BacklogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/backlogs")
@RequiredArgsConstructor
public class BacklogController {

    private final BacklogService backlogService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    public ResponseEntity<JsonResponse> CreateBacklog() throws NotFoundException {
        Backlog backlog = new Backlog();
        backlogService.AddBacklog(backlog);
        return new ResponseEntity<JsonResponse>(new JsonResponse(201, "Backlog created successfully"),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BacklogResponseDTO> getBacklog(@PathVariable Long id) throws NotFoundException {
        BacklogResponseDTO backlogResponseDTO = backlogService.getBacklogById(id);
        return ResponseEntity.ok(backlogResponseDTO);

    }

}
