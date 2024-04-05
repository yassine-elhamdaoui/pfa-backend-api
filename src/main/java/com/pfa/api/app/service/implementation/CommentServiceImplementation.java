package com.pfa.api.app.service.implementation;

import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.Comment;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.CommentRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.CommentService;
import com.pfa.api.app.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentServiceImplementation implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    @Override
    public Comment addComment(CommentDTO commentDTO)
            throws AccessDeniedException, ChangeSetPersister.NotFoundException {
        User user = UserUtils.getCurrentUser(userRepository);

        Comment comment=Comment.dtoToEntity(commentDTO);

        comment.setAuthorId(user.getId());//id of the comment author

        comment=commentRepository.save(comment);
        return comment;
    }

    @Override
    public Comment getCommentById(Long id) {
        return null;
    }

    @Override
    public List<Comment> getComments() {
        return null;
    }

    @Override
    public Comment updateComment(CommentDTO commentDTO, Long id) {
        return null;
    }

    @Override
    public Comment deleteComment(Long id) {
        return null;
    }
}
