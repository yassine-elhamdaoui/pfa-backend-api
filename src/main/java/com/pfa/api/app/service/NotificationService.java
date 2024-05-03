package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.dto.requests.NotificationDTO;
import com.pfa.api.app.entity.Notification;

public interface NotificationService {
    Notification createNotification(NotificationDTO notificationDto);

    void deleteNotification(Long notificationId);

    List<Notification> getAllNotifications();

}
