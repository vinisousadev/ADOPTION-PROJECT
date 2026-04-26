package br.com.adoption.repository;

import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {

    List<Animal> findByStatus(AnimalStatus status);
    Page<Animal> findByStatus(AnimalStatus status, Pageable pageable);
    List<Animal> findByUser_IdOrderById(Long userId);
    Page<Animal> findByUser_Id(Long userId, Pageable pageable);

}
