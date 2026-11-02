package com.gamevault.api.service;

import com.gamevault.api.client.SteamApiClient;
import com.gamevault.api.dto.steam.SteamGameDto;
import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Business logic service for managing the game library and Steam sync
@Service
public class LibraryService {

    private final LibraryRepository repository;
    private final SteamApiClient steamApiClient;

    public LibraryService(LibraryRepository repository, SteamApiClient steamApiClient) {
        this.repository = repository;
        this.steamApiClient = steamApiClient;
    }

    // Returns all games currently saved in the database
    public List<LibraryItem> getAll() {
        return repository.findAll();
    }

    // Fetches single game by its database ID
    public Optional<LibraryItem> getById(Long id) {
        return repository.findById(id);
    }

    // Adds a game manually into the library
    public LibraryItem add(LibraryItem item) {
        if (item.getStatus() == null || item.getStatus().isBlank()) {
            item.setStatus("BACKLOG");
        }
        if (item.getPlaytimeMinutes() == null) {
            item.setPlaytimeMinutes(0);
        }
        return repository.save(item);
    }

    // Removes a single game by id (US-11)
    public boolean deleteById(Long id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Clears all stored games from the database
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    // Imports owned games directly from the Steam Web API and saves them to MySQL
    @Transactional
    public List<LibraryItem> importFromSteam(String steamId) {
        List<SteamGameDto> steamGames = steamApiClient.getOwnedGames(steamId);
        List<LibraryItem> savedItems = new ArrayList<>();

        for (SteamGameDto steamGame : steamGames) {
            if (steamGame.getName() == null || steamGame.getName().trim().isEmpty()) {
                continue; // Skip nameless entries
            }

            // Check if game already exists in library to avoid duplicate records
            Optional<LibraryItem> existing = repository.findBySteamAppId(steamGame.getAppId());

            LibraryItem item;
            if (existing.isPresent()) {
                item = existing.get();
                item.setPlaytimeMinutes(steamGame.getPlaytimeForever());
                if (item.getHeaderImageUrl() == null) {
                    item.setHeaderImageUrl(steamGame.getHeaderImageUrl());
                }
            } else {
                item = new LibraryItem();
                item.setTitle(steamGame.getName().trim());
                item.setSteamAppId(steamGame.getAppId());
                item.setPlaytimeMinutes(steamGame.getPlaytimeForever());
                item.setHeaderImageUrl(steamGame.getHeaderImageUrl());

                // Set initial status based on playtime
                if (steamGame.getPlaytimeForever() != null && steamGame.getPlaytimeForever() > 0) {
                    item.setStatus("PLAYING");
                } else {
                    item.setStatus("BACKLOG");
                }
            }

            savedItems.add(repository.save(item));
        }

        return savedItems;
    }
}
