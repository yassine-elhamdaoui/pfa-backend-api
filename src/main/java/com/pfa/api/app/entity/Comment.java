package com.pfa.api.app.entity;
import com.pfa.api.app.dto.CommentDTO;
import com.pfa.api.app.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;    //comment_id

    @Column(name = "text")
    private String text;   //comment_text

    @Column(name = "date")
    private LocalDateTime date;    //date_when_comment_was_published!!!

    @ManyToOne
    private User author;    //which_supervisor_has_added_this_comment

    @ManyToOne
    private Document document;


    //convert to be able to save a Comment using JPA<Comment,Long>  wich we
    // receive in form of CommentDTOin the request
    public static Comment dtoToEntity(CommentDTO commentDTO){
        return Comment.builder()
                .id(commentDTO.getId())
                .text(commentDTO.getText())
                .build();
    }
}
