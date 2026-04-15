package br.com.adoption.repository;

import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.AdoptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    boolean existsByAnimal_IdAndUser_IdAndStatus(Long animalId, Long userId, AdoptionRequestStatus status);
}