package com.pfa.api.app.dto.requests;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String title;
    private String description;
    private Date creationDate;
    private Long userId;
}
