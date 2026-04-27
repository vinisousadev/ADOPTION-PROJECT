package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Legacy payload for request status update operations")
public class UpdateRequestStatusRequest {

    @Schema(description = "Target user id related to the status change", example = "1")
    @NotNull
    @Positive
    private Long userId;

    public UpdateRequestStatusRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
