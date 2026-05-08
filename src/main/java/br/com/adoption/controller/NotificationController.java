package br.com.adoption.controller;

import br.com.adoption.dto.response.NotificationResponse;
import br.com.adoption.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "User notification endpoints")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "List my notifications")
    public PagedModel<NotificationResponse> getMyNotifications(
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) Authentication authentication) {
        return new PagedModel<>(notificationService.getMyNotifications(pageable, authentication.getName()));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Count unread notifications")
    public Map<String, Long> countUnread(@Parameter(hidden = true) Authentication authentication) {
        return Map.of("count", notificationService.countUnread(authentication.getName()));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public NotificationResponse markAsRead(@PathVariable Long id,
                                           @Parameter(hidden = true) Authentication authentication) {
        return notificationService.markAsRead(id, authentication.getName());
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public void markAllAsRead(@Parameter(hidden = true) Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
    }
}
