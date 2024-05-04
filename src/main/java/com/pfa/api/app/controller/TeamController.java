package com.pfa.api.app.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.TeamDTO;
import com.pfa.api.app.dto.responses.NotificationResponseDTO;
import com.pfa.api.app.dto.responses.TeamResponseDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.service.NotificationService;
import com.pfa.api.app.service.TeamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        TeamResponseDTO team = teamService.getTeamById(id);
        return team != null ? ResponseEntity.ok(team) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@RequestBody TeamDTO teamDTO) throws NotFoundException {
        TeamResponseDTO createdTeam = teamService.createTeam(teamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> updateTeam(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        try {
            TeamResponseDTO updatedTeam = teamService.updateTeam(id, teamDTO);
            return updatedTeam != null ? ResponseEntity.ok(updatedTeam) : ResponseEntity.notFound().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteTeamById(@PathVariable Long id) {
        teamService.deleteTeamById(id);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(200, "Team deleted successfully"), HttpStatus.OK);
    }
}
