package br.com.adoption.repository;

import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.AdoptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long>, JpaSpecificationExecutor<AdoptionRequest> {

    boolean existsByAnimal_IdAndUser_IdAndStatus(Long animalId, Long userId, AdoptionRequestStatus status);

    List<AdoptionRequest> findByAnimal_IdAndStatus(Long animalId, AdoptionRequestStatus status);

    Page<AdoptionRequest> findByUser_Email(String email, Pageable pageable);

    Page<AdoptionRequest> findByAnimal_User_Email(String email, Pageable pageable);

    Page<AdoptionRequest> findByAnimal_User_EmailAndStatusOrderByResponseDateDesc(
            String email,
            AdoptionRequestStatus status,
            Pageable pageable
    );
}
