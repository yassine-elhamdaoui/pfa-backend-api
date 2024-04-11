package com.pfa.api.app.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "file_size")
    private Integer fileSize;

    @ManyToOne(targetEntity = Project.class)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @OneToOne(mappedBy = "report")
    @JsonBackReference
    private Project reportOf;


    @OneToMany(mappedBy = "document" , cascade = CascadeType.ALL)              //mappedBy = "document" <=>specifies that the relationship will be managed by document field in the Comment entity/cascade = CascadeType.ALL<=>when i update a documents  ===>its associated comments will be updated
    @JsonManagedReference
    private List<Comment> comments;

}
