package com.pfa.api.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.UserStory;

public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findBySprintId(Long sprintId);
    List<UserStory> findByBacklogId(Long backLogID);
    
}
