package com.gamevault.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for manually adding a game to prevent mass assignment on JPA entities (Sonar rule java:S4684)
public class CreateGameRequest {

    @NotBlank(message = "Game title is required.")
    @Size(max = 255, message = "Game title cannot exceed 255 characters.")
    private String title;

    private String status = "BACKLOG";

    private Integer playtimeMinutes = 0;

    private String headerImageUrl;

    public CreateGameRequest() {
    }

    public CreateGameRequest(String title, String status, Integer playtimeMinutes, String headerImageUrl) {
        this.title = title;
        this.status = status;
        this.playtimeMinutes = playtimeMinutes;
        this.headerImageUrl = headerImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPlaytimeMinutes() {
        return playtimeMinutes;
    }

    public void setPlaytimeMinutes(Integer playtimeMinutes) {
        this.playtimeMinutes = playtimeMinutes;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }
}
