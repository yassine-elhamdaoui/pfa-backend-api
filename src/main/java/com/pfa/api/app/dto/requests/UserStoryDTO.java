package com.pfa.api.app.dto.requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
public class UserStoryDTO {

    private String name;

    private String description;

    private String status;

    private Long priority;

    private Long storyPoints; 

    private Long developerId;

    private Long sprintId;
    
    private Long backlogId;
    
}
