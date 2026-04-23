package br.com.adoption.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PatchAnimalRequest {

    @Size(max = 100)
    private String animalName;

    @Size(max = 50)
    private String species;

    @Size(max = 100)
    private String breed;

    private LocalDate birthDate;

    @PositiveOrZero
    private Integer age;

    @Size(max = 20)
    private String animalSize;

    private Character sex;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weightKg;

    private Character vaccinated;

    private Character neutered;

    @Size(max = 500)
    private String description;

    public PatchAnimalRequest() {
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
}
