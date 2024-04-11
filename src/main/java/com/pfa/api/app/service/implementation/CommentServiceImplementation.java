package com.pfa.api.app.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.Comment;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.CommentRepository;
import com.pfa.api.app.repository.DocumentRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.CommentService;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImplementation implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Override
    public Comment addComment(CommentDTO commentDTO)
            throws AccessDeniedException, NotFoundException {

        User user = UserUtils.getCurrentUser(userRepository);
        Comment comment=Comment.dtoToEntity(commentDTO);//=id+text+document
        comment.setAuthor(user);


        // get the document entity based on the provided documentid
        Document document = documentRepository.findById(commentDTO.getDocumentId()).get();
        comment.setDocument(document);

        comment.setDate(LocalDateTime.now());

        comment=commentRepository.save(comment);
        return comment;
    }

    @Override
    public Comment getCommentById(Long id) {

        return commentRepository.findById(id).get();
    }

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }


    @Override
    public Comment updateComment(CommentDTO commentDTO, Long id) throws NotFoundException {

        Comment comment = commentRepository.findById(id).orElseThrow(NotFoundException::new);

        User user = UserUtils.getCurrentUser(userRepository);
        comment.setAuthor(user);

        Document document = documentRepository.findById(commentDTO.getDocumentId()).get();
        comment.setDocument(document);

        comment.setText(commentDTO.getText());

        comment=commentRepository.save(comment);
        return comment;

    }

    @Override
    public void deleteComment(Long id) throws NotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            commentRepository.delete(comment);
            return;
        } else {
            throw new NotFoundException();
        }
    }
}
