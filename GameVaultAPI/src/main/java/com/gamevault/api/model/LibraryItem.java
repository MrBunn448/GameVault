package com.gamevault.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// JPA entity representing a game entry in the user's library
@Entity
@Table(name = "library_items")
public class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional Steam App ID if imported from Steam
    @Column(name = "steam_app_id")
    private Long steamAppId;

    @Column(nullable = false)
    private String title;

    // Status: PLAYING, COMPLETED, BACKLOG, DROPPED
    @Column(nullable = false)
    private String status = "BACKLOG";

    // Playtime in minutes (imported from Steam or logged manually)
    @Column(name = "playtime_minutes")
    private Integer playtimeMinutes = 0;

    // Header cover image URL
    @Column(name = "header_image_url")
    private String headerImageUrl;

    public LibraryItem() {
    }

    public LibraryItem(Long id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.playtimeMinutes = 0;
    }

    public LibraryItem(Long id, Long steamAppId, String title, String status, Integer playtimeMinutes, String headerImageUrl) {
        this.id = id;
        this.steamAppId = steamAppId;
        this.title = title;
        this.status = status;
        this.playtimeMinutes = playtimeMinutes != null ? playtimeMinutes : 0;
        this.headerImageUrl = headerImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSteamAppId() {
        return steamAppId;
    }

    public void setSteamAppId(Long steamAppId) {
        this.steamAppId = steamAppId;
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
