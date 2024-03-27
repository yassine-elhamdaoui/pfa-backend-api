package com.pfa.api.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Project;



public interface ProjectService {


    Project addProject(ProjectDTO projectDTO, List<MultipartFile> files) throws AccessDeniedException, NotFoundException;

    Project getProject(Long id) throws NotFoundException;

    List<Project> getAllProject() throws NotFoundException;

    Project updateProject(ProjectDTO projectDTO, Long id) throws NotFoundException;

    Project deleteFile(Long id, Long docId) throws NotFoundException, IOException;

    void validateToken(String approvalToken);

    ///
    public String submitProjectPreference(Map<Long, Integer> projectPreferences) throws NotFoundException;
}