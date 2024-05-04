package com.pfa.api.app.dto.requests;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprintDTO {
    
    private String name;

    private Long velocity;

     private Date start_date;

    private  Date end_date;

    private boolean closed;

    private Long projectId;
    
}
