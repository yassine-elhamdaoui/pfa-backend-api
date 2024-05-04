package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.dto.requests.UserStoryDTO;
import com.pfa.api.app.dto.responses.UserStoryResponseDTO;

public interface UserStoryService {
    

    UserStoryResponseDTO addUserStory(UserStoryDTO userStoryDTO);

    UserStoryResponseDTO getUserStory(Long id);

     List<UserStoryResponseDTO>  getAllUserStory(Long id);

    UserStoryResponseDTO updateUserStory(UserStoryDTO userStoryDTO ,Long id);


    UserStoryResponseDTO deleteUserStory(Long id);

    UserStoryResponseDTO AffectDevelopToUserStory(Long id,Long developId);

    UserStoryResponseDTO AffectSprintToUserStory(Long id,Long sprintId);


}
