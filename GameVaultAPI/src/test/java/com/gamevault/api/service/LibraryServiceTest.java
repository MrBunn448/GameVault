package com.gamevault.api.service;

import com.gamevault.api.client.SteamApiClient;
import com.gamevault.api.dto.steam.SteamGameDto;
import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit tests for LibraryService business logic using Mockito
@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository repository;

    @Mock
    private SteamApiClient steamApiClient;

    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService(repository, steamApiClient);
    }

    @Test
    void getAll_shouldReturnAllItemsFromRepository() {
        // Arrange: prepare dummy data
        List<LibraryItem> mockItems = List.of(
                new LibraryItem(1L, "Portal 2", "COMPLETED"),
                new LibraryItem(2L, "Terraria", "PLAYING")
        );
        when(repository.findAll()).thenReturn(mockItems);

        // Act
        List<LibraryItem> result = service.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Portal 2", result.get(0).getTitle());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getById_whenItemExists_shouldReturnItem() {
        // Arrange
        LibraryItem item = new LibraryItem(1L, "Hades", "PLAYING");
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        Optional<LibraryItem> result = service.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hades", result.get().getTitle());
    }

    @Test
    void getById_whenItemDoesNotExist_shouldReturnEmpty() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<LibraryItem> result = service.getById(99L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void add_whenStatusIsBlank_shouldDefaultToBacklog() {
        // Arrange
        LibraryItem input = new LibraryItem();
        input.setTitle("Celeste");
        input.setStatus(""); // blank status

        when(repository.save(any(LibraryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LibraryItem saved = service.add(input);

        // Assert
        assertEquals("BACKLOG", saved.getStatus());
        assertEquals(0, saved.getPlaytimeMinutes());
        verify(repository).save(input);
    }

    @Test
    void importFromSteam_shouldFetchFromApiAndSaveGames() {
        // Arrange: mock Steam API response with 2 games
        List<SteamGameDto> steamGames = List.of(
                new SteamGameDto(620L, "Portal 2", 180, "icon_hash_1"),
                new SteamGameDto(105600L, "Terraria", 0, "icon_hash_2")
        );
        when(steamApiClient.getOwnedGames("76561198000000000")).thenReturn(steamGames);
        when(repository.findBySteamAppId(anyLong())).thenReturn(Optional.empty());
        when(repository.save(any(LibraryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<LibraryItem> result = service.importFromSteam("76561198000000000");

        // Assert
        assertEquals(2, result.size());

        // Portal 2 has playtime, should be set to PLAYING
        LibraryItem portal = result.get(0);
        assertEquals("Portal 2", portal.getTitle());
        assertEquals(620L, portal.getSteamAppId());
        assertEquals(180, portal.getPlaytimeMinutes());
        assertEquals("PLAYING", portal.getStatus());

        // Terraria has 0 playtime, should be set to BACKLOG
        LibraryItem terraria = result.get(1);
        assertEquals("Terraria", terraria.getTitle());
        assertEquals("BACKLOG", terraria.getStatus());

        verify(repository, times(2)).save(any(LibraryItem.class));
    }

    @Test
    void importFromSteam_whenGameAlreadyExists_shouldUpdatePlaytimeInsteadOfDuplicating() {
        // Arrange: existing game in db
        LibraryItem existingGame = new LibraryItem(5L, 620L, "Portal 2", "PLAYING", 60, "old_url");

        List<SteamGameDto> steamGames = List.of(
                new SteamGameDto(620L, "Portal 2", 240, "icon_hash_1")
        );
        when(steamApiClient.getOwnedGames("76561198000000000")).thenReturn(steamGames);
        when(repository.findBySteamAppId(620L)).thenReturn(Optional.of(existingGame));
        when(repository.save(any(LibraryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<LibraryItem> result = service.importFromSteam("76561198000000000");

        // Assert: should update existing item's playtime to 240
        assertEquals(1, result.size());
        assertEquals(240, result.get(0).getPlaytimeMinutes());
        assertEquals(5L, result.get(0).getId());
    }

    @Test
    void deleteById_whenItemExists_shouldDeleteAndReturnTrue() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = service.deleteById(1L);

        assertTrue(result);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenItemDoesNotExist_shouldReturnFalse() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean result = service.deleteById(999L);

        assertFalse(result);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAll_shouldCallRepositoryDeleteAll() {
        service.deleteAll();

        verify(repository, times(1)).deleteAll();
    }
}
