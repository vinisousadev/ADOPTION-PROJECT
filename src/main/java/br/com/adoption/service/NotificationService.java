package br.com.adoption.service;

import br.com.adoption.dto.response.NotificationResponse;
import br.com.adoption.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationResponse> getMyNotifications(Pageable pageable, String userEmail);

    long countUnread(String userEmail);

    NotificationResponse markAsRead(Long notificationId, String userEmail);

    void markAllAsRead(String userEmail);

    void notify(User recipient,
                String title,
                String message,
                String type,
                String relatedEntityType,
                Long relatedEntityId,
                String actionUrl);
}
