package br.com.adoption.dto.request;

import br.com.adoption.entity.AgeUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload for creating a new animal")
public class CreateAnimalRequest {

    @Schema(description = "Display name of the animal", example = "Mel")
    @NotBlank
    @Size(max = 100)
    private String animalName;

    @Schema(description = "Species of the animal", example = "Dog")
    @NotBlank
    @Size(max = 50)
    private String species;

    @Schema(description = "Breed of the animal", example = "Labrador")
    @Size(max = 100)
    private String breed;

    @Schema(description = "Birth date of the animal", example = "2022-01-15")
    private LocalDate birthDate;

    @Schema(description = "Animal age value", example = "6")
    @PositiveOrZero
    private Integer ageValue;

    @Schema(description = "Animal age unit", example = "MONTHS", allowableValues = {"MONTHS", "YEARS"})
    private AgeUnit ageUnit;

    @Schema(description = "Size category of the animal", example = "MEDIUM")
    @Size(max = 20)
    private String animalSize;

    @Schema(description = "Sex of the animal", example = "F", allowableValues = {"M", "F"})
    private Character sex;

    @Schema(description = "Weight in kilograms", example = "12.50")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weightKg;

    @Schema(description = "Whether the animal is vaccinated", example = "Y", allowableValues = {"Y", "N"})
    @NotNull
    private Character vaccinated;

    @Schema(description = "Whether the animal is neutered", example = "N", allowableValues = {"Y", "N"})
    @NotNull
    private Character neutered;

    @Schema(description = "Additional details about the animal", example = "Very friendly and used to children")
    @Size(max = 500)
    private String description;

    public CreateAnimalRequest() {
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

    public Integer getAgeValue() {
        return ageValue;
    }

    public void setAgeValue(Integer ageValue) {
        this.ageValue = ageValue;
    }

    public AgeUnit getAgeUnit() {
        return ageUnit;
    }

    public void setAgeUnit(AgeUnit ageUnit) {
        this.ageUnit = ageUnit;
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

    @AssertTrue(message = "ageValue and ageUnit must be provided together")
    public boolean isAgeComplete() {
        return (ageValue == null && ageUnit == null) || (ageValue != null && ageUnit != null);
    }
}
