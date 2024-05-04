package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.UserStoryDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.dto.responses.UserStoryResponseDTO;
import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.entity.Sprint;
import com.pfa.api.app.entity.UserStory;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.BacklogRepository;
import com.pfa.api.app.repository.SprintRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.repository.UserStoryRepository;
import com.pfa.api.app.service.UserStoryService;

import lombok.RequiredArgsConstructor;





@Service
@RequiredArgsConstructor
public class UserStoryImplementation implements UserStoryService {
    private final UserStoryRepository userStoryRepository;
    private final BacklogRepository backlogRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    
    @Override
    public UserStoryResponseDTO addUserStory(UserStoryDTO userStoryDTO) {
       
            Backlog backlog = backlogRepository.findById(userStoryDTO.getBacklogId()).orElseThrow(
                    () -> new RuntimeException("Backlog not found")
            );
            
             UserStory userStory =UserStory.builder()
                               .name(userStoryDTO.getName())
                               .description(userStoryDTO.getDescription())
                               .priority(userStoryDTO.getPriority())
                               .storyPoints(userStoryDTO.getStoryPoints())
                               .status(userStoryDTO.getStatus())
                               .backlog(backlog)
                               .build();

            if (userStoryDTO.getDeveloperId() != null) {
                User develop = userRepository.findById(userStoryDTO.getDeveloperId()).orElseThrow(
                        () -> new RuntimeException("Develop not found")
                );
                userStory.setDeveloper(develop);
            }
         UserStory savUserStory =  userStoryRepository.save(userStory);
        return UserStoryResponseDTO.fromEntity(savUserStory);
    }

    @Override
    public UserStoryResponseDTO deleteUserStory(Long id) {
        UserStory userStory =userStoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("UserStory not found")
        );
        userStoryRepository.delete(userStory);
        return UserStoryResponseDTO.fromEntity(userStory) ;
    }

    @Override
    public List<UserStoryResponseDTO>  getAllUserStory(Long id) {
        
        List<UserStory> userStories = userStoryRepository.findAll();
        List<UserStory> userStoriesFilteredByBacklog = new ArrayList<>();
        for (UserStory userStory : userStories) {
            if (userStory.getBacklog().getId().equals(id)) {
                userStoriesFilteredByBacklog.add(userStory);
            }
        }
        return userStoriesFilteredByBacklog.stream()
                .map(UserStoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UserStoryResponseDTO getUserStory(Long id) {
        UserStory userStory = userStoryRepository.findById(id).orElseThrow (() -> new RuntimeException("userStory not found"));
        return UserStoryResponseDTO.fromEntity(userStory);
    }

    @Override
    public UserStoryResponseDTO updateUserStory(UserStoryDTO userStoryDTO ,Long id) {
               UserStory userStory = userStoryRepository.findById(id).orElseThrow (() -> new RuntimeException("userStory not found"));

               if (userStoryDTO.getName()!=null) {
                  userStory.setName(userStoryDTO.getName());
               }
               if(userStoryDTO.getDescription()!=null){
                   userStory.setDescription(userStoryDTO.getDescription());
               }
               if (userStoryDTO.getStatus()!=null) {
                   userStory.setStatus(userStoryDTO.getStatus());
               }
               if(userStoryDTO.getStoryPoints()!=null){
                    userStory.setStoryPoints(userStoryDTO.getStoryPoints());
               }
               if (userStoryDTO.getPriority()!=null) {
                userStory.setPriority(userStoryDTO.getPriority());;
                
               }

             userStoryRepository.save(userStory);  
              
        return UserStoryResponseDTO.fromEntity(userStory);
    }
  
    @Override
    public UserStoryResponseDTO  AffectDevelopToUserStory(Long id,Long developId){
    
        UserStory userStory  = userStoryRepository.findById(id).orElseThrow (() -> new RuntimeException("userStory not found"));
        User develop = userRepository.findById(developId).orElseThrow (() -> new RuntimeException("Develop not found"));
         
        userStory.setDeveloper(develop);
        userStoryRepository.save(userStory);
        
  return UserStoryResponseDTO.fromEntity(userStory);
   } 
   @Override
    public UserStoryResponseDTO  AffectSprintToUserStory(Long id,Long sprintId){
    
        UserStory userStory  = userStoryRepository.findById(id).orElseThrow (() -> new RuntimeException("userStory not found"));
        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow (() -> new RuntimeException("Sprint not found"));
         
        userStory.setSprint(sprint);
        userStoryRepository.save(userStory);
        
  return UserStoryResponseDTO.fromEntity(userStory);
   }
    
}


