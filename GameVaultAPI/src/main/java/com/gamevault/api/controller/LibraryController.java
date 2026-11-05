package com.gamevault.api.controller;

import com.gamevault.api.dto.CreateGameRequest;
import com.gamevault.api.dto.steam.SteamImportRequest;
import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST controller for game library endpoints
@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:3000",
        "http://[2001:67c:6ec:7331:1c01:11ff:fe01:55f4]:3000"
})
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // US-10: Get all games in library
    @GetMapping
    public List<LibraryItem> getAll() {
        return libraryService.getAll();
    }

    // Get specific game by id
    @GetMapping("/{id}")
    public ResponseEntity<LibraryItem> getById(@PathVariable Long id) {
        return libraryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // US-09: Add game manually using DTO to prevent Mass Assignment vulnerability (Sonar rule java:S4684)
    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody CreateGameRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Game title is required.");
        }
        LibraryItem item = new LibraryItem();
        item.setTitle(request.getTitle().trim());
        item.setStatus(request.getStatus() != null && !request.getStatus().trim().isEmpty()
                ? request.getStatus().trim()
                : "BACKLOG");
        item.setPlaytimeMinutes(request.getPlaytimeMinutes() != null ? request.getPlaytimeMinutes() : 0);
        item.setHeaderImageUrl(request.getHeaderImageUrl() != null && !request.getHeaderImageUrl().trim().isEmpty()
                ? request.getHeaderImageUrl().trim()
                : null);

        LibraryItem saved = libraryService.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // US-11: Delete single game from library
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boolean deleted = libraryService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Clear all games from database (temporary?)
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        libraryService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    // US-09 / US-08: Import owned games from Steam Web API (handles JSON body from frontend)
    @PostMapping(value = "/steam/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importFromSteamJson(@RequestBody SteamImportRequest request) {
        return executeSteamImport(request != null ? request.getSteamId() : null);
    }

    // US-09 / US-08: Import owned games from Steam Web API (handles query param fallback)
    @PostMapping(value = "/steam/import")
    public ResponseEntity<?> importFromSteamParam(@RequestParam(required = false) String steamId) {
        return executeSteamImport(steamId);
    }

    private ResponseEntity<?> executeSteamImport(String steamId) {
        if (steamId == null || steamId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Steam ID is required.");
        }

        try {
            List<LibraryItem> imported = libraryService.importFromSteam(steamId.trim());
            return ResponseEntity.ok(imported);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to sync with Steam: " + e.getMessage());
        }
    }
}
