package com.pfa.api.app.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private User author;    //which_supervisor_has_added_this_comment

    @ManyToOne
    @JsonBackReference
    private Document document;


    //convert to be able to save a Comment using JPA<Comment,Long>  wich we
    // receive in form of CommentDTOin the request
    public static Comment dtoToEntity(CommentDTO commentDTO){
        return Comment.builder()
                .text(commentDTO.getText())
                .build();
    }
}
