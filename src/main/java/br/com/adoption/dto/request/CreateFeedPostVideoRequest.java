package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Feed post video creation payload")
public class CreateFeedPostVideoRequest {

    @NotBlank
    @Size(max = 500)
    @Schema(description = "Public video URL", example = "https://example.com/feed/video.mp4")
    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
