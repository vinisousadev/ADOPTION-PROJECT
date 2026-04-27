package br.com.adoption.dto.response;

import br.com.adoption.entity.AnimalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Animal response payload")
public class AnimalResponse {

    @Schema(description = "Unique animal identifier", example = "1")
    private Long id;
    @Schema(description = "Display name of the animal", example = "Mel")
    private String animalName;
    @Schema(description = "Species of the animal", example = "Dog")
    private String species;
    @Schema(description = "Breed of the animal", example = "Labrador")
    private String breed;
    @Schema(description = "Birth date of the animal", example = "2022-01-15")
    private LocalDate birthDate;
    @Schema(description = "Age in years", example = "3")
    private Integer age;
    @Schema(description = "Size category of the animal", example = "MEDIUM")
    private String animalSize;
    @Schema(description = "Sex of the animal", example = "F", allowableValues = {"M", "F"})
    private Character sex;
    @Schema(description = "Weight in kilograms", example = "12.50")
    private BigDecimal weightKg;
    @Schema(description = "Whether the animal is vaccinated", example = "Y", allowableValues = {"Y", "N"})
    private Character vaccinated;
    @Schema(description = "Whether the animal is neutered", example = "N", allowableValues = {"Y", "N"})
    private Character neutered;
    @Schema(description = "Additional details about the animal", example = "Very friendly and used to children")
    private String description;
    @Schema(description = "Current animal status", example = "AVAILABLE", allowableValues = {"AVAILABLE", "ADOPTED", "REMOVED"})
    private AnimalStatus status;
    @Schema(description = "Date and time when the animal was registered", example = "2026-04-27T10:15:30")
    private LocalDateTime registrationDate;
    @Schema(description = "Owner user id", example = "1")
    private Long userId;

    public AnimalResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public Character getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(Character vaccinated) {
        this.vaccinated = vaccinated;
    }

    public Character getNeutered() {
        return neutered;
    }

    public void setNeutered(Character neutered) {
        this.neutered = neutered;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnimalStatus getStatus() {
        return status;
    }

    public void setStatus(AnimalStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
