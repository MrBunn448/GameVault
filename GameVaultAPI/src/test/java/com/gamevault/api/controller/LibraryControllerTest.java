package com.gamevault.api.controller;

import com.gamevault.api.dto.CreateLibraryItemRequest;
import com.gamevault.api.dto.LibraryItemResponse;
import com.gamevault.api.exception.ResourceNotFoundException;
import com.gamevault.api.model.GameStatus;
import com.gamevault.api.service.LibraryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LibraryService libraryService;

    @Nested
    @DisplayName("GET /api/library (US-10: View Library)")
    class GetLibraryTests {

        @Test
        @DisplayName("Should return 200 OK and list of library items")
        void shouldReturnLibraryItems() throws Exception {
            List<LibraryItemResponse> items = List.of(
                    new LibraryItemResponse(1L, "Hades", GameStatus.PLAYING, 9, 40, LocalDate.now(), null),
                    new LibraryItemResponse(2L, "Elden Ring", GameStatus.COMPLETED, 10, 120, LocalDate.now(), LocalDate.now())
            );

            when(libraryService.getAllLibraryItems()).thenReturn(items);

            mockMvc.perform(get("/api/library")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].title", is("Hades")))
                    .andExpect(jsonPath("$[0].status", is("PLAYING")))
                    .andExpect(jsonPath("$[1].title", is("Elden Ring")))
                    .andExpect(jsonPath("$[1].status", is("COMPLETED")));
        }

        @Test
        @DisplayName("Should return 200 OK when library item exists by ID")
        void shouldReturnItemById() throws Exception {
            LibraryItemResponse item = new LibraryItemResponse(1L, "Hades", GameStatus.PLAYING, 9, 40, LocalDate.now(), null);
            when(libraryService.getLibraryItemById(1L)).thenReturn(item);

            mockMvc.perform(get("/api/library/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("Hades")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when library item does not exist")
        void shouldReturn404WhenNotFound() throws Exception {
            when(libraryService.getLibraryItemById(999L))
                    .thenThrow(new ResourceNotFoundException("Library entry with id 999 not found."));

            mockMvc.perform(get("/api/library/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not Found")));
        }
    }

    @Nested
    @DisplayName("POST /api/library (US-09: Add Game to Library)")
    class AddGameTests {

        @Test
        @DisplayName("Should return 201 Created with valid payload and Location header")
        void shouldCreateLibraryItem() throws Exception {
            CreateLibraryItemRequest request = new CreateLibraryItemRequest(
                    "Celeste",
                    GameStatus.PLAYING,
                    9,
                    15,
                    null,
                    null
            );

            LibraryItemResponse response = new LibraryItemResponse(
                    3L,
                    "Celeste",
                    GameStatus.PLAYING,
                    9,
                    15,
                    LocalDate.now(),
                    null
            );

            when(libraryService.addGameToLibrary(any(CreateLibraryItemRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/library")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/library/3"))
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.title", is("Celeste")))
                    .andExpect(jsonPath("$.status", is("PLAYING")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            CreateLibraryItemRequest invalidRequest = new CreateLibraryItemRequest(
                    "",
                    GameStatus.BACKLOG,
                    null,
                    0,
                    null,
                    null
            );

            mockMvc.perform(post("/api/library")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.details.title").exists());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when status is null")
        void shouldReturn400WhenStatusNull() throws Exception {
            String jsonWithoutStatus = """
                    {
                        "title": "Portal 2"
                    }
                    """;

            mockMvc.perform(post("/api/library")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonWithoutStatus))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")))
                    .andExpect(jsonPath("$.details.status").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/library/{id}")
    class DeleteGameTests {

        @Test
        @DisplayName("Should return 204 No Content on successful deletion")
        void shouldDeleteGame() throws Exception {
            doNothing().when(libraryService).removeGameFromLibrary(1L);

            mockMvc.perform(delete("/api/library/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 Not Found when deleting non-existent item")
        void shouldReturn404OnDeleteMissingItem() throws Exception {
            doThrow(new ResourceNotFoundException("Not found")).when(libraryService).removeGameFromLibrary(99L);

            mockMvc.perform(delete("/api/library/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
