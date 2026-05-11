package br.com.adoption.repository;

import br.com.adoption.entity.AdoptionRequestMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestMessageRepository extends JpaRepository<AdoptionRequestMessage, Long> {
    Page<AdoptionRequestMessage> findByAdoptionRequest_Id(Long adoptionRequestId, Pageable pageable);
}
