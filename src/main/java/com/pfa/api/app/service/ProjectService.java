package com.pfa.api.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.ProjectDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.dto.responses.TeamPreferenceResponseDTO;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.entity.user.User;



public interface ProjectService {


    ProjectResponseDTO addProject(ProjectDTO projectDTO, List<MultipartFile> files ,MultipartFile report) throws AccessDeniedException, NotFoundException;

    ProjectResponseDTO getProject(Long id) throws NotFoundException;

    List<ProjectResponseDTO> getAllProjects(int pageNumber,int pageSize , String academicYear) throws NotFoundException;
    List<String> getAllAcademicYears();


    ProjectResponseDTO updateProject(ProjectDTO projectDTO, Long id,
            List<MultipartFile> files,MultipartFile report) throws NotFoundException;

    ProjectResponseDTO deleteFile(Long id, Long docId) throws NotFoundException, IOException;

    void validateToken(String approvalToken);

    void approveProject(Long id) throws NotFoundException;

    void rejectProject(Long id) throws NotFoundException;

    public String submitProjectPreference(Map<Long, Integer> projectPreferences) throws NotFoundException;

    List<TeamPreference> getAllProjectsPreferences();

    List<TeamPreferenceResponseDTO> getAllProjectsPreferencesResponse();

    Map<User,Project> assignUsersToProjects() throws NotFoundException;

    void validateAssignments() throws NotFoundException;

    ResponseEntity<byte[]> downloadFile(Long projectId, Long docId);

    

}