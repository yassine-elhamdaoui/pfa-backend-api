package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.SprintDTO;
import com.pfa.api.app.dto.responses.SprintResponse;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.Sprint;
import com.pfa.api.app.entity.UserStory;
import com.pfa.api.app.repository.ProjectRepository;
import com.pfa.api.app.repository.SprintRepository;
import com.pfa.api.app.repository.UserStoryRepository;
import com.pfa.api.app.service.SprintService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintServiceImplementation implements SprintService{
  private final SprintRepository sprintRepository;
  private final ProjectRepository projectRepository;
  private final UserStoryRepository userStoryRepository;
    @Override
    public SprintResponse AddSprint(SprintDTO sprintDTO) {
        Project project = projectRepository.findById(sprintDTO.getProjectId()).orElseThrow (() -> new RuntimeException("Project not found"));
        Sprint sprint = Sprint.builder()
                .name(sprintDTO.getName())
                .start_date(sprintDTO.getStart_date())
                .end_date(sprintDTO.getEnd_date())
                .velocity(sprintDTO.getVelocity())
                
                .project(project)
                .build();
                sprintRepository.save(sprint);
       return SprintResponse.fromEntity(sprint);
    }

    @Override
    public SprintResponse updateSprint(SprintDTO sprintDTO, Long id) {

        Sprint sprint = sprintRepository.findById(id).orElseThrow (() -> new RuntimeException("Sprint not found"));
                 if (sprintDTO.getName()!=null) {
                    sprint.setName(sprintDTO.getName());
                 }
                 if (sprintDTO.getVelocity()!=null) {
                    sprint.setVelocity(sprintDTO.getVelocity());
                 }
                 if (sprintDTO.getEnd_date()!=null) {
                    sprint.setEnd_date(sprintDTO.getEnd_date());
                 }
                 if (sprintDTO.getStart_date()!=null) {
                    sprint.setStart_date(sprintDTO.getStart_date());
                 }
                 if (sprintDTO.isClosed()) {
                    sprint.setClosed(sprintDTO.isClosed());
                }
                
                 
                 
                 
                 sprintRepository.save(sprint);
                 return SprintResponse.fromEntity(sprint);
                 
        
    }

    @Override
    public SprintResponse getSprint(Long id) {
        
        List<UserStory> filteredBySprint = userStoryRepository.findBySprintId(id);
        
        Sprint sprint = sprintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sprint not found"));
    
        
        sprint.setUserStories(filteredBySprint);
    
        
        return SprintResponse.fromEntity(sprint);
    }
    

    @Override
    public SprintResponse deleteSprint(Long id) {

         Sprint sprint = sprintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sprint not found"));

        sprintRepository.delete(sprint);
        
       return SprintResponse.fromEntity(sprint);
    }

    @Override
    public List<SprintResponse> getAllSprints(Long projectId) {
       List<Sprint> sprintResponses = sprintRepository.findByProjectId(projectId);
       return sprintResponses.stream()
             .map(SprintResponse::fromEntity)
             .collect(Collectors.toList());
    }
    
}
