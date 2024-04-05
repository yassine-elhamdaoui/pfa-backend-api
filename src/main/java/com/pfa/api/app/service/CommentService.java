package com.pfa.api.app.service;

import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.Comment;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;

public interface CommentService {

    public Comment addComment(CommentDTO commentDTO) throws ChangeSetPersister.NotFoundException;
    public Comment getCommentById(Long id);
    public List<Comment> getComments();
    public Comment updateComment(CommentDTO commentDTO,Long id);
    public Comment deleteComment(Long id);

}
