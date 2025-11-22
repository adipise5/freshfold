package com.freshfold.dto;

import java.time.LocalDateTime;

public class StatusUpdateRequest {
    private String status;

    public StatusUpdateRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}