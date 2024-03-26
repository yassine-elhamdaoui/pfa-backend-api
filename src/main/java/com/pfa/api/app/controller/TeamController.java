package com.pfa.api.app.controller;

import com.pfa.api.app.dto.TeamDTO;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody TeamDTO teamDTO) throws NotFoundException {
        Team createdTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    @DeleteMapping("delete/{teamId}")
    public ResponseEntity<String> deleteTeamById(@PathVariable Long teamId) {
        teamService.deleteTeamById(teamId);
        return ResponseEntity.ok("Team with ID " + teamId + " deleted successfully");
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    @DeleteMapping("delete/{teamName}")
    public ResponseEntity<String> deleteTeamByName(@PathVariable String teamName) {
        teamService.deleteTeamByName(teamName);
        return ResponseEntity.ok("Team with name " + teamName + " deleted successfully");
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    @PutMapping("update/{teamId}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @RequestBody TeamDTO teamDTO)
            throws NotFoundException {
        Team updatedTeam = teamService.updateTeam(teamId, teamDTO);
        if (updatedTeam != null) {
            return ResponseEntity.ok(updatedTeam);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE','ROLE_USER','ROLE_SUPERVISOR','ROLE_HEAD_OF_BRANCH')")
    @GetMapping("/{teamId}")
    public ResponseEntity<Object> getTeamById(@PathVariable Long teamId) {
        Team team = teamService.getTeamById(teamId);
        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE','ROLE_USER','ROLE_SUPERVISOR','ROLE_HEAD_OF_BRANCH')")
    @GetMapping("/{teamName}")
    public ResponseEntity<Object> getTeamByName(@PathVariable String teamName) {
        Team team = teamService.getTeamByName(teamName);
        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_RESPONSIBLE','ROLE_USER','ROLE_SUPERVISOR','ROLE_HEAD_OF_BRANCH')")
    @GetMapping("/all")
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

}
