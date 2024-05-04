package com.pfa.api.app.service;

import java.util.List;
import java.util.Optional;

import com.pfa.api.app.dto.responses.BacklogResponseDTO;
// import com.pfa.api.app.dto.responses.BacklogResponseDTO;
import com.pfa.api.app.entity.Backlog;

public interface BacklogService {
      Backlog AddBacklog(Backlog backlog); 
      BacklogResponseDTO getBacklogById(Long id);
      
      


}
