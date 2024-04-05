package com.pfa.api.app.controller;
import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.Comment;
import com.pfa.api.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping
//    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<JsonResponse> addNewComment(@RequestBody CommentDTO commentDTO) throws ChangeSetPersister.NotFoundException {
        Comment comment=commentService.addComment(commentDTO);
        return new ResponseEntity<JsonResponse>(new JsonResponse(
                200,
                "comment added successfully"),
                HttpStatus.OK
        );
    }
}
