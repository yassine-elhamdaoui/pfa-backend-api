package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfa.api.app.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
