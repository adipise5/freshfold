package com.freshfold.dto;

import java.time.LocalDateTime;

public class StatusUpdateRequest {
    private String status;
    private LocalDateTime timestamp;

    public StatusUpdateRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
