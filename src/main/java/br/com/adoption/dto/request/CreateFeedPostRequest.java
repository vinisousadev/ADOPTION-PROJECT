package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Feed post creation payload")
public class CreateFeedPostRequest {

    @NotBlank
    @Size(max = 1000)
    @Schema(description = "Post text content", example = "A Luna encontrou uma nova familia!")
    private String content;

    @Schema(description = "Optional animal linked to this post", example = "1")
    private Long animalId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }
}
