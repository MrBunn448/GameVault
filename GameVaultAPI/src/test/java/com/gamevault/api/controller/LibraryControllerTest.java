package com.gamevault.api.controller;

import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web layer tests verifying HTTP request handling, response codes, and serialization
@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibraryService libraryService;

    @Test
    void shouldReturnLibraryItems() throws Exception {
        when(libraryService.getAll()).thenReturn(List.of(
                new LibraryItem(1L, "Hades", "PLAYING"),
                new LibraryItem(2L, "Elden Ring", "COMPLETED")
        ));

        mockMvc.perform(get("/api/library"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Hades")))
                .andExpect(jsonPath("$[1].title", is("Elden Ring")));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        when(libraryService.getById(1L)).thenReturn(Optional.of(new LibraryItem(1L, "Hades", "PLAYING")));

        mockMvc.perform(get("/api/library/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Hades")));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(libraryService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/library/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateLibraryItem() throws Exception {
        when(libraryService.add(any(LibraryItem.class)))
                .thenReturn(new LibraryItem(3L, "Celeste", "PLAYING"));

        String json = "{\"title\": \"Celeste\", \"status\": \"PLAYING\"}";

        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Celeste")));
    }

    @Test
    void shouldReturn400WhenTitleBlank() throws Exception {
        String json = "{\"title\": \"\", \"status\": \"BACKLOG\"}";

        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldImportFromSteamSuccessfully() throws Exception {
        List<LibraryItem> imported = List.of(
                new LibraryItem(10L, 620L, "Portal 2", "PLAYING", 120, "https://cdn.example.com/portal2.jpg")
        );
        when(libraryService.importFromSteam("76561198000000000")).thenReturn(imported);

        mockMvc.perform(post("/api/library/steam/import")
                        .param("steamId", "76561198000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Portal 2")))
                .andExpect(jsonPath("$[0].steamAppId", is(620)));
    }

    @Test
    void shouldReturn400WhenSteamIdMissing() throws Exception {
        mockMvc.perform(post("/api/library/steam/import"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteGameById() throws Exception {
        when(libraryService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/library/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentGame() throws Exception {
        when(libraryService.deleteById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/library/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllGames() throws Exception {
        mockMvc.perform(delete("/api/library"))
                .andExpect(status().isNoContent());
    }
}
