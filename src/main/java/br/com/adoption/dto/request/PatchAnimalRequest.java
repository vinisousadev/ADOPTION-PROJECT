package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload for partially updating an animal")
public class PatchAnimalRequest {

    @Schema(description = "Display name of the animal", example = "Mel")
    @Size(max = 100)
    private String animalName;

    @Schema(description = "Species of the animal", example = "Dog")
    @Size(max = 50)
    private String species;

    @Schema(description = "Breed of the animal", example = "Labrador")
    @Size(max = 100)
    private String breed;

    @Schema(description = "Birth date of the animal", example = "2022-01-15")
    private LocalDate birthDate;

    @Schema(description = "Age in years", example = "3")
    @PositiveOrZero
    private Integer age;

    @Schema(description = "Size category of the animal", example = "MEDIUM")
    @Size(max = 20)
    private String animalSize;

    @Schema(description = "Sex of the animal", example = "F", allowableValues = {"M", "F"})
    private Character sex;

    @Schema(description = "Weight in kilograms", example = "12.50")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weightKg;

    @Schema(description = "Whether the animal is vaccinated", example = "Y", allowableValues = {"Y", "N"})
    private Character vaccinated;

    @Schema(description = "Whether the animal is neutered", example = "N", allowableValues = {"Y", "N"})
    private Character neutered;

    @Schema(description = "Additional details about the animal", example = "Very friendly and used to children")
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
