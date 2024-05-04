package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.entity.Notification;

public interface NotificationService {
    void deleteNotification(Long notificationId);

    List<Notification> getNotificationsByUserId(Long userId);

    List<Notification> getAllNotifications();

}
