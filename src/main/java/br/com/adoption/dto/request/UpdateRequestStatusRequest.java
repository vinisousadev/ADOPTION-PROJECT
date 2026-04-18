package br.com.adoption.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateRequestStatusRequest {

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