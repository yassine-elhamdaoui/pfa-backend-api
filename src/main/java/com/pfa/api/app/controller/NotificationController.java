package com.pfa.api.app.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pfa.api.app.dto.requests.NotificationDTO;
import com.pfa.api.app.dto.responses.NotificationResponseDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@RequestBody NotificationDTO notificationDto) {
        Notification createdNotification = notificationService.createNotification(notificationDto);
        NotificationResponseDTO responseDTO = NotificationResponseDTO.fromEntity(createdNotification);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationResponseDTO> responseDTOs = notifications.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
