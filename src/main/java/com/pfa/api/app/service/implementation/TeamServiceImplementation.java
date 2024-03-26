package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.TeamDTO;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.TeamService;
import com.pfa.api.app.util.UserUtils;

@Service
public class TeamServiceImplementation implements TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Team createTeam(TeamDTO teamDTO) throws NotFoundException {
        // Obtenir l'utilisateur actuel en utilisant UserUtils
        User currentUser = UserUtils.getCurrentUser(userRepository);

        // Récupérer la liste des rôles de l'utilisateur actuel
        List<Role> roles = new ArrayList<>();
        Role responsibleRole = roleRepository.findByName(RoleName.ROLE_RESPONSIBLE);
        roles.add(responsibleRole);

        // Associer les rôles à l'utilisateur
        currentUser.setRoles(roles);

        // Enregistrer l'utilisateur dans la base de données
        userRepository.save(currentUser);

        List<User> members = new ArrayList<>();
        for (Long memberId : teamDTO.getMembersIds()) {
            User member = userRepository.findById(memberId).orElse(null);
            if (member != null) {
                members.add(member);
            }
        }

        // Créer l'équipe en utilisant les données du DTO
        Team team = Team.builder()
                .name(teamDTO.getName())
                .responsible(currentUser)
                .members(members)
                .build();

        // Enregistrer l'équipe dans la base de données
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeamById(Long teamId) {
        teamRepository.deleteById(teamId);
    }

    @Override
    public void deleteTeamByName(String teamName) {
        Team team = teamRepository.findByName(teamName);
        if (team != null) {
            teamRepository.delete(team);
        }
    }

    @Override
    public Team updateTeam(Long teamId, TeamDTO teamDTO) throws NotFoundException {
        Team existingTeam = teamRepository.findById(teamId).get();

        // Obtenir l'utilisateur actuel en utilisant UserUtils
        User currentUser = UserUtils.getCurrentUser(userRepository);

        List<User> members = new ArrayList<>();
        for (Long memberId : teamDTO.getMembersIds()) {
            User member = userRepository.findById(memberId).get();
            members.add(member);
        }

        existingTeam.setName(teamDTO.getName());
        existingTeam.setResponsible(currentUser);
        existingTeam.setMembers(members);

        return teamRepository.save(existingTeam);
    }

    @Override
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    @Override
    public Team getTeamByName(String teamName) {
        return teamRepository.findByName(teamName);
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}
