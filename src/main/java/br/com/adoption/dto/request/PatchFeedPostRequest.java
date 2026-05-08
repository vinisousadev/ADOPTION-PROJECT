package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Feed post partial update payload")
public class PatchFeedPostRequest {

    @Size(max = 1000)
    @Schema(description = "Post text content", example = "A Luna encontrou uma nova familia!")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
