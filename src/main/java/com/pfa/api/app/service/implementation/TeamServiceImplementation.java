package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.TeamDTO;
import com.pfa.api.app.dto.responses.TeamResponseDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.TeamService;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImplementation implements TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public TeamResponseDTO createTeam(TeamDTO teamDTO) throws NotFoundException {
        // Obtenir l'utilisateur actuel en utilisant UserUtils
        User currentUser = UserUtils.getCurrentUser(userRepository);
        if (currentUser.getTeam() != null) {
            throw new RuntimeException("you're already in a team");
        }
        // Associer les rôles à l'utilisateur

        // Enregistrer l'utilisateur dans la base de données

        List<User> members = new ArrayList<>();
        for (Long memberId : teamDTO.getMembersIds()) {
            User member = userRepository.findById(memberId).orElse(null);
            if (member.getTeam() != null) {
                throw new RuntimeException("you entered a member that is already in a team");
            }
            members.add(member);
        }

        // Créer l'équipe en utilisant les données du DTO
        Team team = Team.builder()
                .name(teamDTO.getName())
                .members(members)
                .responsible(currentUser)
                .build();
        team.getMembers().add(currentUser);
        Team persistedTeam = teamRepository.save(team);
        if (!currentUser.getRoles().stream().map(Role::getName)
                .anyMatch(name -> name.equals(RoleName.ROLE_RESPONSIBLE.toString()))) {
            currentUser.getRoles().add(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());
        }

        currentUser.setTeam(persistedTeam);
        userRepository.save(currentUser);

        for (User member : members) {
            member.setTeam(persistedTeam);
        }
        userRepository.saveAll(members);

        // Créer une notification pour chaque membre de l'équipe
        for (User member : members) {
            // Vérifier si le membre n'est pas le responsable de l'équipe
            if (!member.equals(currentUser)) {
                Notification notification = Notification.builder()
                        .description("The User "
                                + currentUser.getFirstName() + " " + currentUser.getLastName() +
                                " has added you as a member of team " + persistedTeam.getName() +
                                ". Review and contact him please.")
                        .creationDate(new Date())
                        .nameOfSender(currentUser.getFirstName() + " " + currentUser.getLastName())
                        .user(member)
                        .type("team")
                        .build();

                // Enregistrer la notification dans la base de données
                notificationRepository.save(notification);
            }
        }

        // Enregistrer l'équipe dans la base de données
        return TeamResponseDTO.fromEntity(persistedTeam);
    }

    @SuppressWarnings("null")
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
    public TeamResponseDTO updateTeam(Long teamId, TeamDTO teamDTO) throws NotFoundException {
        // Récupérer l'équipe à modifier de la base de données
        Team existingTeam = teamRepository.findById(teamId).orElseThrow(NotFoundException::new);

        // Récupérer le nom complet de l'ancien responsable
        String oldResponsibleName = existingTeam.getResponsible().getFirstName() + " "
                + existingTeam.getResponsible().getLastName();

        // Vérifier si l'utilisateur actuel est le responsable de l'équipe
        User currentUser = UserUtils.getCurrentUser(userRepository);
        if (!currentUser.equals(existingTeam.getResponsible())) {
            throw new RuntimeException("Only the responsible of the team can update it");
        }

        // Mettre à jour le nom de l'équipe si présent dans le DTO et différent de la
        // valeur actuelle
        if (teamDTO.getName() != null && !teamDTO.getName().equals(existingTeam.getName())) {
            existingTeam.setName(teamDTO.getName());
        }

        if (teamDTO.getNewResponsible() != null
                && !teamDTO.getNewResponsible().equals(existingTeam.getResponsible().getId())) {
            Long newResponsibleId = teamDTO.getNewResponsible();
            User newResponsible = userRepository.findById(newResponsibleId).orElseThrow(NotFoundException::new);

            // Vérifier si le nouveau responsable fait déjà partie de l'équipe
            if (!existingTeam.getMembers().contains(newResponsible)) {
                // Retirer le rôle de responsable à l'ancien responsable
                existingTeam.getResponsible().getRoles()
                        .remove(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());

                // Ajouter le rôle de responsable au nouveau responsable
                newResponsible.getRoles().add(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());
                // Mettre à jour le nouveau responsable et son équipe
                existingTeam.setResponsible(newResponsible);
                newResponsible.setTeam(existingTeam);
            } else {
                // Ajouter le rôle de responsable au nouveau responsable s'il fait déjà partie
                // de l'équipe
                newResponsible.getRoles().add(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());
                existingTeam.setResponsible(newResponsible);

            }
            Notification newResponsibleNotification = Notification.builder()
                    .description("The user" + oldResponsibleName + " has assigned you as the new responsible for team "
                            + existingTeam.getName() + ".")
                    .creationDate(new Date())
                    .nameOfSender(oldResponsibleName)
                    .user(newResponsible)
                    .type("team")
                    .build();
            notificationRepository.save(newResponsibleNotification);
        }

        // Mettre à jour les membres de l'équipe si présents dans le DTO
        if (teamDTO.getMembersIds() != null && !teamDTO.getMembersIds().isEmpty())

        {
            List<User> updatedMembers = new ArrayList<>();
            for (Long memberId : teamDTO.getMembersIds()) {
                User member = userRepository.findById(memberId).orElseThrow(NotFoundException::new);
                // Vérifier si le membre n'appartient à aucune équipe et n'est pas déjà présent
                // dans l'équipe
                if (member.getTeam() == null && !existingTeam.getMembers().contains(member)) {
                    // Ajouter le membre à l'équipe
                    member.setTeam(existingTeam);
                    updatedMembers.add(member);

                    // Envoyer une notification au nouveau membre
                    Notification newMemberNotification = Notification.builder()
                            .description("The user" + oldResponsibleName + " has added you as a member of the team "
                                    + existingTeam.getName() + ".")
                            .creationDate(new Date())
                            .nameOfSender(oldResponsibleName)
                            .user(member)
                            .type("team")
                            .build();
                    notificationRepository.save(newMemberNotification);

                }
            }
            // Mettre à jour la liste des membres de l'équipe
            existingTeam.getMembers().addAll(updatedMembers);
        }

        // Mettre à null la référence à l'équipe pour les membres qui ne sont pas inclus
        // dans les nouveaux membres
        existingTeam.getMembers().stream().filter(member -> !teamDTO.getMembersIds().contains(member.getId()))
                .forEach(member -> {
                    member.setTeam(null);
                    // Envoyer une notification au membre supprimé avec le nom de l'ancien
                    // responsable dans la description
                    if (!member.equals(currentUser)) {
                        Notification removedMemberNotification = Notification.builder()
                                .description("The user " + oldResponsibleName + " has removed you from the team "
                                        + existingTeam.getName() + ". You are no longer a member of this team.")
                                .creationDate(new Date())
                                .nameOfSender(oldResponsibleName)
                                .user(member)
                                .type("team")
                                .build();
                        notificationRepository.save(removedMemberNotification);

                    }

                });
        // Enregistrer les modifications dans la base de données
        return TeamResponseDTO.fromEntity(teamRepository.save(existingTeam));
    }

    @Override
    public TeamResponseDTO getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        return TeamResponseDTO.fromEntity(team);
    }

    @Override
    public TeamResponseDTO getTeamByName(String teamName) {
        Team team = teamRepository.findByName(teamName);
        return TeamResponseDTO.fromEntity(team);
    }

    @Override
    public List<TeamResponseDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        List<TeamResponseDTO> teamResponseDTOs = new ArrayList<>();
        for (Team team : teams) {
            TeamResponseDTO teamResponseDTO = TeamResponseDTO.fromEntity(team);
            teamResponseDTOs.add(teamResponseDTO);
        }
        return teamResponseDTOs;
    }
}