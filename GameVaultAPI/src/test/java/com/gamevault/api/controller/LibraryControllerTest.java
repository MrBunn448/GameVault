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
//Simple test just to have something to run in CI/CD pipline

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    // MockMvc sends requests through Spring MVC without starting a real web server.
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibraryService libraryService;

    @Test
    void shouldReturnLibraryItems() throws Exception {
        // define what the controller's service dependency returns.
        when(libraryService.getAll()).thenReturn(List.of(
                new LibraryItem(1L, "Hades", "PLAYING"),
                new LibraryItem(2L, "Elden Ring", "COMPLETED")
        ));

        mockMvc.perform(get("/api/library")) //call api endpoint
                .andExpect(status().isOk())
                // "$" is the complete JSON array; "$[0].title" selects the first game's title.
                .andExpect(jsonPath("$", hasSize(2))) //$ is root of json
                .andExpect(jsonPath("$[0].title", is("Hades")))
                .andExpect(jsonPath("$[1].title", is("Elden Ring")));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        // The Optional contains a game, so the controller should respond with 200 and that game.
        // optional is java definition of "Can be null"
        when(libraryService.getById(1L)).thenReturn(Optional.of(new LibraryItem(1L, "Hades", "PLAYING")));

        mockMvc.perform(get("/api/library/1")) //get id 1 in library
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Hades")));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        // An empty Optional represents an ID that the service could not find.
        when(libraryService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/library/999")) // non-existent id
                .andExpect(status().isNotFound()); //expect 404 not found
    }

    @Test
    void shouldCreateLibraryItem() throws Exception {
        // Match any LibraryItem; the mock returns a saved-shaped item with an assigned ID.
        when(libraryService.add(any(LibraryItem.class)))
                .thenReturn(new LibraryItem(3L, "Celeste", "PLAYING"));

        String json = "{\"title\": \"Celeste\", \"status\": \"PLAYING\"}";

        mockMvc.perform(post("/api/library")
                        // Tell Spring to parse this request body as JSON.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Celeste")));
    }

    @Test
    void shouldReturn400WhenTitleBlank() throws Exception {
        String json = "{\"title\": \"\", \"status\": \"BACKLOG\"}";

        // The controller rejects the blank title before it needs to call the mocked service.
        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
