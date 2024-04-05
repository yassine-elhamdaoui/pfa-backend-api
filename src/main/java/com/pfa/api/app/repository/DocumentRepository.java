package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Document;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document , Long>{
    Optional<Document> findByDocName(String docName);
    Optional<Document> findById(Long id);
}
