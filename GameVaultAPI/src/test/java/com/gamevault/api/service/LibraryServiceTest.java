package com.gamevault.api.service;

import com.gamevault.api.dto.CreateLibraryItemRequest;
import com.gamevault.api.dto.LibraryItemResponse;
import com.gamevault.api.exception.ResourceNotFoundException;
import com.gamevault.api.model.GameStatus;
import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository repository;

    private LibraryServiceImpl libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryServiceImpl(repository);
    }

    @Test
    @DisplayName("getAllLibraryItems should return mapped DTOs from repository")
    void shouldReturnAllLibraryItems() {
        List<LibraryItem> items = List.of(
                new LibraryItem(1L, "Hades", GameStatus.PLAYING, 9, 40, LocalDate.now(), null),
                new LibraryItem(2L, "Elden Ring", GameStatus.COMPLETED, 10, 120, LocalDate.now(), LocalDate.now())
        );

        when(repository.findAll()).thenReturn(items);

        List<LibraryItemResponse> result = libraryService.getAllLibraryItems();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Hades");
        assertThat(result.get(1).title()).isEqualTo("Elden Ring");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("getLibraryItemById should return item if found")
    void shouldReturnItemById() {
        LibraryItem item = new LibraryItem(1L, "Hades", GameStatus.PLAYING, 9, 40, LocalDate.now(), null);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        LibraryItemResponse result = libraryService.getLibraryItemById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Hades");
    }

    @Test
    @DisplayName("getLibraryItemById should throw ResourceNotFoundException when missing")
    void shouldThrowWhenItemMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.getLibraryItemById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("addGameToLibrary should auto-fill completedDate when status is COMPLETED")
    void shouldAutoFillCompletedDateForCompletedGames() {
        CreateLibraryItemRequest request = new CreateLibraryItemRequest(
                "Celeste",
                GameStatus.COMPLETED,
                10,
                20,
                LocalDate.of(2026, 1, 1),
                null
        );

        when(repository.save(any(LibraryItem.class))).thenAnswer(invocation -> {
            LibraryItem item = invocation.getArgument(0);
            item.setId(5L);
            return item;
        });

        LibraryItemResponse response = libraryService.addGameToLibrary(request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.title()).isEqualTo("Celeste");
        assertThat(response.completedDate()).isEqualTo(LocalDate.now());

        ArgumentCaptor<LibraryItem> captor = ArgumentCaptor.forClass(LibraryItem.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCompletedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("removeGameFromLibrary should delete item when exists")
    void shouldRemoveExistingItem() {
        when(repository.deleteById(1L)).thenReturn(true);

        libraryService.removeGameFromLibrary(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("removeGameFromLibrary should throw ResourceNotFoundException when item missing")
    void shouldThrowOnRemoveMissingItem() {
        when(repository.deleteById(99L)).thenReturn(false);

        assertThatThrownBy(() -> libraryService.removeGameFromLibrary(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
