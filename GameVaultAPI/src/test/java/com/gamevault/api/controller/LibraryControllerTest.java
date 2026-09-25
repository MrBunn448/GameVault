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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("$[0].title", is("Hades")));
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
}
