package com.gamevault.api.model;

import java.time.LocalDate;

/**
 * Domain entity representing a game entry in the user's personal GameVault library.
 */
public class LibraryItem {

    private Long id;
    private String title;
    private GameStatus status;
    private Integer personalRating;
    private Integer playtimeHours;
    private LocalDate startDate;
    private LocalDate completedDate;

    public LibraryItem() {
    }

    public LibraryItem(Long id, String title, GameStatus status, Integer personalRating, Integer playtimeHours, LocalDate startDate, LocalDate completedDate) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.personalRating = personalRating;
        this.playtimeHours = playtimeHours != null ? playtimeHours : 0;
        this.startDate = startDate;
        this.completedDate = completedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
