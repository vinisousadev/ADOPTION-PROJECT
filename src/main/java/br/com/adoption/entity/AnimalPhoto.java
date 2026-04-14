package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;

@Entity
@Table(name = "ANIMAL_PHOTO")
@SequenceGenerator(
        name = "animal_photo_seq",
        sequenceName = "SEQ_ANIMAL_PHOTO",
        allocationSize = 1
)
public class AnimalPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animal_photo_seq")
    @Column(name = "ID_PHOTO")
    private Long id;

    @Column(name = "PHOTO_URL", nullable = false, length = 255)
    private String photoUrl;

    @Column(name = "IS_MAIN", nullable = false, length = 1)
    private Character isMain;

    @ManyToOne
    @JoinColumn(name = "FK_ANIMAL_ID_ANIMAL", nullable = false)
    private Animal animal;

    public AnimalPhoto() {
    }

    public Long getId() {
        return id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Character getIsMain() {
        return isMain;
    }

    public void setIsMain(Character isMain) {
        this.isMain = isMain;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}