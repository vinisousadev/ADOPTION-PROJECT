package br.com.adoption.entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ANIMAL")
@SequenceGenerator(
        name = "animal_seq",
        sequenceName = "SEQ_ANIMAL",
        allocationSize = 1
)

public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animal_seq")
    @Column(name = "ID_ANIMAL")
    private Long id;

    @Column(name = "ANIMAL_NAME", nullable = false, length = 100)
    private String animalName;

    @Column(name = "SPECIES", nullable = false, length = 50)
    private String species;

    @Column(name = "BREED", length = 100)
    private String breed;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "ANIMAL_SIZE", length = 20)
    private String animalSize;

    @Column(name = "SEX", length = 1)
    private Character sex;

    @Column(name = "WEIGHT_KG", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "VACCINATED", nullable = false, length = 1)
    private Character vaccinated;

    @Column(name = "NEUTERED", nullable = false, length = 1)
    private Character neutered;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private LocalDateTime registrationDate;

    @ManyToOne
    @JoinColumn(name = "FK_USERS_ID_USER")
    private User user;

    public Animal() {
    }

    public Long getId() {
        return id;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAnimalSize() {
        return animalSize;
    }

    public void setAnimalSize(String animalSize) {
        this.animalSize = animalSize;
    }

    public Character  getSex() {
        return sex;
    }

    public void setSex(Character  sex) {
        this.sex = sex;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public Character  getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(Character  vaccinated) {
        this.vaccinated = vaccinated;
    }

    public Character  getNeutered() {
        return neutered;
    }

    public void setNeutered(Character  neutered) {
        this.neutered = neutered;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
