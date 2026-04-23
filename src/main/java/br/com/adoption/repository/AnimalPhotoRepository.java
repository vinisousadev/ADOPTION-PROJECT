package br.com.adoption.repository;

import br.com.adoption.entity.AnimalPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Long> {
    Page<AnimalPhoto> findByAnimal_Id(Long animalId, Pageable pageable);
}
