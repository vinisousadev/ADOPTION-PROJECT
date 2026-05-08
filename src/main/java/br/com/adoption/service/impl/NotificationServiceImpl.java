package br.com.adoption.service.impl;

import br.com.adoption.dto.response.NotificationResponse;
import br.com.adoption.entity.Notification;
import br.com.adoption.entity.User;
import br.com.adoption.exception.OnlyOwnerCanManageUserException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.NotificationMapper;
import br.com.adoption.repository.NotificationRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(Pageable pageable, String userEmail) {
        User authenticatedUser = findUserByEmail(userEmail);

        return notificationRepository.findByRecipient_Id(authenticatedUser.getId(), pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    public long countUnread(String userEmail) {
        User authenticatedUser = findUserByEmail(userEmail);
        return notificationRepository.countByRecipient_IdAndReadAtIsNull(authenticatedUser.getId());
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId, String userEmail) {
        User authenticatedUser = findUserByEmail(userEmail);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        validateRecipient(notification, authenticatedUser);

        if (notification.getReadAt() == null) {
            notification.setReadAt(OffsetDateTime.now());
        }

        return NotificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(String userEmail) {
        User authenticatedUser = findUserByEmail(userEmail);
        OffsetDateTime now = OffsetDateTime.now();

        notificationRepository.findByRecipient_IdAndReadAtIsNull(authenticatedUser.getId())
                .forEach(notification -> {
                    notification.setReadAt(now);
                    notificationRepository.save(notification);
                });
    }

    @Override
    public void notify(User recipient,
                       String title,
                       String message,
                       String type,
                       String relatedEntityType,
                       Long relatedEntityId,
                       String actionUrl) {
        if (recipient == null || recipient.getId() == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setActionUrl(actionUrl);
        notification.setCreatedAt(OffsetDateTime.now());

        notificationRepository.save(notification);
    }

    private void validateRecipient(Notification notification, User authenticatedUser) {
        boolean isRecipient = notification.getRecipient() != null
                && notification.getRecipient().getId().equals(authenticatedUser.getId());

        if (!isRecipient) {
            throw new OnlyOwnerCanManageUserException("Only the notification recipient can manage this notification");
        }
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
