package com.coursework.story.service;

import com.coursework.story.dto.NotificationDTO;
import com.coursework.story.model.Notification;
import com.coursework.story.model.User;
import com.coursework.story.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public void send(User recipient, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotifications() {
        User user = userService.getAuthenticatedUser().orElseThrow(() -> new RuntimeException("User not found"));
        return user.getNotifications()
                .stream()
                .sorted(Comparator.comparing(Notification::getTimestamp).reversed())
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(List<Long> notificationIds) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.markAsReadByIds(notificationIds, user);
    }
}