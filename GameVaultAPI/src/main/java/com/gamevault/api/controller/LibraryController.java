package com.gamevault.api.controller;

import com.gamevault.api.dto.CreateLibraryItemRequest;
import com.gamevault.api.dto.LibraryItemResponse;
import com.gamevault.api.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST Controller exposing personal game library resources.
 * Implements endpoints for US-10 (View Library) and US-09 (Add Game to Library).
 */
@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * US-10: Retrieve all games currently tracked in the user's library.
     *
     * @return 200 OK with list of library game cards
     */
    @GetMapping
    public ResponseEntity<List<LibraryItemResponse>> getLibrary() {
        List<LibraryItemResponse> items = libraryService.getAllLibraryItems();
        return ResponseEntity.ok(items);
    }
    /*
     * @param id the library item ID
     * @return 200 OK with game details, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<LibraryItemResponse> getLibraryItem(@PathVariable Long id) {
        LibraryItemResponse item = libraryService.getLibraryItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * US-09: Add a new game to the personal library with tracking status.
     *
     * @param request validated payload with game title, status, and progress
     * @return 201 Created with Location header and saved game details
     */
    @PostMapping
    public ResponseEntity<LibraryItemResponse> addGameToLibrary(@Valid @RequestBody CreateLibraryItemRequest request) {
        LibraryItemResponse createdItem = libraryService.addGameToLibrary(request);
        URI location = URI.create("/api/library/" + createdItem.id());
        return ResponseEntity.created(location).body(createdItem);
    }

    /**
     * Remove a game from the library.
     *
     * @param id the library item ID
     * @return 204 No Content, or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameFromLibrary(@PathVariable Long id) {
        libraryService.removeGameFromLibrary(id);
        return ResponseEntity.noContent().build();
    }
}
