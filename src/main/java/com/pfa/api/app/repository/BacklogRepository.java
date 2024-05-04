package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Backlog;
import java.util.List;


public interface BacklogRepository extends JpaRepository<Backlog,Long> {

    Backlog findByProjectId(Long projectId);

    
    
}
