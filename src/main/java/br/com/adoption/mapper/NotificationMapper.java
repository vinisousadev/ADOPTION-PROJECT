package br.com.adoption.mapper;

import br.com.adoption.dto.response.NotificationResponse;
import br.com.adoption.entity.Notification;

public class NotificationMapper {

    public static NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setActionUrl(notification.getActionUrl());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        response.setRead(notification.getReadAt() != null);

        return response;
    }
}
