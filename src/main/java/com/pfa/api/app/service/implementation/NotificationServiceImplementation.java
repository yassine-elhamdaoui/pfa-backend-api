package com.pfa.api.app.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.service.NotificationService;

@Service
public class NotificationServiceImplementation implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        long userIdPrimitive = userId.longValue();
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getUser().getId() == userIdPrimitive)
                .collect(Collectors.toList());
    }
}
