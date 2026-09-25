package com.gamevault.api.controller;

import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class LibraryController {
//test
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public List<LibraryItem> getAll() {
        return libraryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryItem> getById(@PathVariable Long id) {
        return libraryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody LibraryItem item) {
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Game title is required.");
        }
        item.setTitle(item.getTitle().trim());
        LibraryItem saved = libraryService.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
