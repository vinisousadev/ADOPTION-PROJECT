package br.com.adoption.repository;

import br.com.adoption.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipient_Id(Long recipientId, Pageable pageable);

    long countByRecipient_IdAndReadAtIsNull(Long recipientId);

    List<Notification> findByRecipient_IdAndReadAtIsNull(Long recipientId);
}
