package com.pfa.api.app.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import com.pfa.api.app.entity.Notification;

@Builder
@Getter
@Setter
public class NotificationResponseDTO {
    private Long id;
    private String description;
    private Date creationDate;
    private Long userId;
    private String nameOfSender;
    private String type;

    public static NotificationResponseDTO fromEntity(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .description(notification.getDescription())
                .creationDate(notification.getCreationDate())
                .userId(notification.getUser().getId())
                .nameOfSender(notification.getNameOfSender())
                .type(notification.getType())
                .build();
    }
}
