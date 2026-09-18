package com.gamevault.api.dto;

import com.gamevault.api.model.GameStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new game in the user's library.
 * Validates incoming client payloads according to US-09.
 */
public class CreateLibraryItemRequest {

    @NotBlank(message = "Game title is required and cannot be blank.")
    @Size(max = 150, message = "Game title must not exceed 150 characters.")
    private String title;

    @NotNull(message = "Game status is required (BACKLOG, PLAYING, COMPLETED, DROPPED).")
    private GameStatus status;

    @Min(value = 1, message = "Personal rating must be at least 1.")
    @Max(value = 10, message = "Personal rating cannot exceed 10.")
    private Integer personalRating;

    @Min(value = 0, message = "Playtime hours cannot be negative.")
    private Integer playtimeHours;

    private LocalDate startDate;
    private LocalDate completedDate;

    public CreateLibraryItemRequest() {
    }

    public CreateLibraryItemRequest(String title, GameStatus status, Integer personalRating, Integer playtimeHours, LocalDate startDate, LocalDate completedDate) {
        this.title = title;
        this.status = status;
        this.personalRating = personalRating;
        this.playtimeHours = playtimeHours;
        this.startDate = startDate;
        this.completedDate = completedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Integer getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(Integer personalRating) {
        this.personalRating = personalRating;
    }

    public Integer getPlaytimeHours() {
        return playtimeHours;
    }

    public void setPlaytimeHours(Integer playtimeHours) {
        this.playtimeHours = playtimeHours;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }
}
