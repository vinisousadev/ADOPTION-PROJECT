package br.com.adoption.repository;

import br.com.adoption.entity.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByStatus(String status);
    Page<Animal> findByStatus(String status, Pageable pageable);
    List<Animal> findByUser_IdOrderById(Long userId);
    Page<Animal> findByUser_Id(Long userId, Pageable pageable);

}
