package com.pfa.api.app.entity;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfa.api.app.entity.user.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class UserStory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Long storyPoints;

  @Column(nullable = false)
  private Long priority;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private String description;

  @ManyToOne(targetEntity = User.class, optional = true)
  @JoinColumn(name = "developer_id")
  @JsonBackReference
  private User developer;

  @ManyToOne(targetEntity = Backlog.class)
  @JoinColumn(name = "backlog_id")
  @JsonBackReference
  private Backlog backlog;

  @ManyToOne(targetEntity = Sprint.class, optional = true)
  @JoinColumn(name = "sprint_id")
  @JsonBackReference
  private Sprint sprint;

}
