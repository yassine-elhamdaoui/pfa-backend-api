package com.pfa.api.app.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfa.api.app.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status;

    private String techStack;

    private String codeLink;

    private String academicYear;

    private Boolean  isPublic;

    @JsonIgnore
    private String approvalToken;

    @OneToOne
    @JoinColumn(name = "report_id")
    @JsonManagedReference
    private Document report;

    @ManyToOne(targetEntity = Branch.class)
    @JoinColumn(name="branch_id")
    @JsonBackReference
    private Branch branch;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "project_supervisor", 
    joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "supervisor_id", 
    referencedColumnName = "id"))
    @JsonIgnore
    private List<User>  supervisors ;

    @OneToMany(mappedBy = "project" , fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Document> documents;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;
         


    @OneToOne
    @JoinColumn(name = "backlog_id")
    @JsonManagedReference
     private Backlog backlog; 
     
     @OneToMany(mappedBy = "project")
     @JsonManagedReference
     private List<Sprint> sprints;
    
  
    
}



