package com.pfa.api.app.dto.responses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Sprint;
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
public class SprintResponse {
    private Long id;
    private String name;
    private Date starDate;
    private Date endDate;
    private Long velocity;
    private boolean closed;

    private Long projectID;

    private List<UserStoryResponseDTO> userStories;

    public static SprintResponse fromEntity(Sprint sprint) {
        List<UserStory> userStoryIds = null;
        if (sprint.getUserStories() != null) {

            userStoryIds = sprint.getUserStories().stream()
                    .collect(Collectors.toList());
        }

        return SprintResponse.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .starDate(sprint.getStart_date())
                .endDate(sprint.getEnd_date())
                .velocity(sprint.getVelocity())
                .closed(sprint.isClosed())
                .projectID(sprint.getProject() == null ? null : sprint.getProject().getId())
                .userStories(fromEntity(sprint.getUserStories()))
                .build();
    }

    public static List<UserStoryResponseDTO> fromEntity(List<UserStory> userStories) {
        List<UserStoryResponseDTO> responseDTOs = new ArrayList<>();
        for (UserStory userStory : userStories) {
            responseDTOs.add(UserStoryResponseDTO.fromEntity(userStory));
        }
        return responseDTOs;
    }

}
