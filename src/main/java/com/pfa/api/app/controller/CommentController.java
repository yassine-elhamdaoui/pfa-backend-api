package com.pfa.api.app.controller;
import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.Comment;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.service.CommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<JsonResponse> addNewComment(@RequestBody CommentDTO commentDTO) throws ChangeSetPersister.NotFoundException {
        Comment comment=commentService.addComment(commentDTO);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(
                200,
                "comment added successfully")
                ,
                HttpStatus.OK
        );
    }


    @GetMapping
    public ResponseEntity<List<Comment>> getComments() throws ChangeSetPersister.NotFoundException {
        List<Comment> comments=commentService.getComments();
        return new ResponseEntity<List<Comment>>(comments,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        Comment comment=commentService.getCommentById(id);
        return new ResponseEntity<Comment>(comment,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Comment> updateComment(@RequestBody CommentDTO commentDTO,@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        Comment comment=commentService.updateComment(commentDTO,id);
        return new ResponseEntity<Comment>(comment,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteComment(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        commentService.deleteComment(id);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(
                        200,
                        "comment deleted successfully")
                ,
                HttpStatus.OK
        );
    }







}
