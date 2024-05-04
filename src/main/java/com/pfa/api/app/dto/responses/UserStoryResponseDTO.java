package com.pfa.api.app.dto.responses;

import com.pfa.api.app.entity.UserStory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long priority;
    private Long storyPoints; 

    private UserResponseDTO developer;

    private Long sprintId;
    private Long backlogId;

   public static UserStoryResponseDTO fromEntity(UserStory userStory) {

        return UserStoryResponseDTO.builder()
                .id(userStory.getId())
                .name(userStory.getName())
                .description(userStory.getDescription())
                .status(userStory.getStatus())
                .priority(userStory.getPriority())
                .storyPoints(userStory.getStoryPoints())
                .developer(userStory.getDeveloper()==null ? null : UserResponseDTO.fromEntity(userStory.getDeveloper()))
                .sprintId(userStory.getSprint()==null ? null : userStory.getSprint().getId())
                .backlogId(userStory.getBacklog().getId())
                .build();
    }
    


    
    
    
}
