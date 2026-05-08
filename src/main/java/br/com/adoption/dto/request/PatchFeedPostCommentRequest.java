package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Feed post comment partial update payload")
public class PatchFeedPostCommentRequest {

    @Size(max = 500)
    @Schema(description = "Comment text content", example = "Que noticia linda!")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
