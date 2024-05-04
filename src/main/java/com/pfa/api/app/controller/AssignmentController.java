package com.pfa.api.app.controller;

import com.pfa.api.app.dto.responses.AssignmentResponseDTO;
import com.pfa.api.app.entity.Assignment;
import com.pfa.api.app.service.AssignmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    
    private final AssignmentService assignmentService;

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> getAssignmentById(@PathVariable Long id) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(id);
            return ResponseEntity.ok(AssignmentResponseDTO.fromEntity(assignment));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<AssignmentResponseDTO> getAssignmentByYear(@RequestParam String year) throws NotFoundException {
        Assignment assignment = assignmentService.getAssignmentByYear(year);
        return ResponseEntity.ok(AssignmentResponseDTO.fromEntity(assignment));
    }
}
