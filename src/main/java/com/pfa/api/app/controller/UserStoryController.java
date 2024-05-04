package com.pfa.api.app.controller;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.UserStoryDTO;
import com.pfa.api.app.dto.responses.UserStoryResponseDTO;
import com.pfa.api.app.service.UserStoryService;

import lombok.RequiredArgsConstructor;





@RestController
@RequestMapping("/api/user-stories")
@RequiredArgsConstructor
public class UserStoryController {
    private final UserStoryService userStoryService;
    

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
    public ResponseEntity<JsonResponse> CreateUserStory(@RequestBody UserStoryDTO userStoryDTO)throws NotFoundException {
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.addUserStory(userStoryDTO);
       return new ResponseEntity<JsonResponse>(new JsonResponse(201, "UserStory created successfully"), HttpStatus.CREATED) ;
        
        
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserStoryResponseDTO> getUserStoryById(@PathVariable Long id)throws NotFoundException{
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.getUserStory(id);
        return ResponseEntity.ok(userStoryResponseDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> updateUserStory(@PathVariable Long id, @RequestBody UserStoryDTO userStoryDTO)throws NotFoundException {
          UserStoryResponseDTO userStoryResponseDTO = userStoryService.updateUserStory(userStoryDTO, id);
        
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "UserStory has been updated"), HttpStatus.ACCEPTED);
    }

   @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteUserStory(@PathVariable Long id )throws NotFoundException {
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.deleteUserStory(id);
    
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "UserStory has been Deleted"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/affect-developer")
    public ResponseEntity<JsonResponse> AffectDevelopToUserStory(@PathVariable Long id, @RequestParam Long developId)throws NotFoundException {
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.AffectDevelopToUserStory(id, developId);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "Developer has been affected to UserStory"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/affect-sprint")
    public ResponseEntity<JsonResponse> AffectSprintToUserStory(@PathVariable Long id, @RequestParam Long sprintId)throws NotFoundException {
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.AffectSprintToUserStory(id, sprintId);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "Sprint has been affected to UserStory"), HttpStatus.ACCEPTED);
    }
    
    
    
}
