package com.pfa.api.app.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
public class Sprint {
    
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
    @Column(nullable = false)
        private String name;

    @Column(nullable = false)
    private Long velocity;

    @Column(nullable = false)
    private Date start_date;

    @Column(nullable = false)
    private  Date end_date;
    
    private boolean closed;
    
    @OneToMany(mappedBy = "sprint")
    @JsonManagedReference
    private List <UserStory> userStories;



    @ManyToOne(targetEntity =Project.class )
    @JoinColumn(name = "project_id")
    @JsonBackReference
     private Project project;


}
