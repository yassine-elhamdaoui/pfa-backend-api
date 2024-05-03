package com.pfa.api.app.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.NotificationDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.NotificationService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class NotificationServiceImplementation implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Notification createNotification(NotificationDTO notificationDto) {
        Notification notification = new Notification();
        notification.setTitle(notificationDto.getTitle());
        notification.setDescription(notificationDto.getDescription());
        notification.setCreationDate(notificationDto.getCreationDate());
        User user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with id: " + notificationDto.getUserId()));
        notification.setUser(user);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
