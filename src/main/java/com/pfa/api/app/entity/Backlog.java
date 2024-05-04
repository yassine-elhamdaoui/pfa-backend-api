package com.pfa.api.app.entity;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Backlog {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id ;

    @OneToOne(mappedBy = "backlog")
    @JsonBackReference
    private Project project;
    
    @OneToMany(mappedBy = "backlog")
    @JsonManagedReference
    private List<UserStory> userStories;
}
