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

            // Steam App IDs identify games, so use one to decide whether to create or update a library row.
            Optional<LibraryItem> existing = repository.findBySteamAppId(steamGame.getAppId());

            LibraryItem item;
            if (existing.isPresent()) {
                // Reuse the saved row so its database ID, title, and the user's status are preserved.
                item = existing.get();
                // Steam is the source of truth for playtime, so refresh it on every import.
                item.setPlaytimeMinutes(steamGame.getPlaytimeForever());
                // Keep a saved image URL; only fill it from Steam-derived data when it is missing.
                if (item.getHeaderImageUrl() == null) {
                    item.setHeaderImageUrl(steamGame.getHeaderImageUrl());
                }
            } else {
                // This App ID is not in the library yet, so create a new database entity from Steam's data.
                item = new LibraryItem();
                item.setTitle(steamGame.getName().trim());
                item.setSteamAppId(steamGame.getAppId());
                item.setPlaytimeMinutes(steamGame.getPlaytimeForever());
                item.setHeaderImageUrl(steamGame.getHeaderImageUrl());

                // Give new entries a useful initial status; existing user-selected statuses are left unchanged.
                if (steamGame.getPlaytimeForever() != null && steamGame.getPlaytimeForever() > 0) {
                    item.setStatus("PLAYING");
                } else {
                    item.setStatus("BACKLOG");
                }
            }

            // save inserts new entities or updates existing ones; collect the saved result for the HTTP response.
            savedItems.add(repository.save(item));
        }

        return savedItems;
    }
}
