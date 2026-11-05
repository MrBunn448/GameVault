package com.gamevault.api.integration;

import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Integration tests verifying full slice from HTTP JSON -> Controller -> Service -> Repository -> H2 DB
@SpringBootTest
@AutoConfigureMockMvc
class LibraryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LibraryRepository libraryRepository;

    @BeforeEach
    void setUp() {
        libraryRepository.deleteAll();
    }

    @Test
    void addGame_shouldPersistToRepositoryAndReturnValidJson() throws Exception {
        // Arrange: valid JSON payload matching API contract
        String requestJson = "{"
                + "\"title\": \"Cyberpunk 2077\","
                + "\"status\": \"PLAYING\","
                + "\"playtimeMinutes\": 180,"
                + "\"headerImageUrl\": \"https://cdn.example.com/cyberpunk.jpg\""
                + "}";

        // Act & Assert (HTTP JSON -> Controller -> Service -> Repository -> DB -> Response JSON)
        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Cyberpunk 2077")))
                .andExpect(jsonPath("$.status", is("PLAYING")))
                .andExpect(jsonPath("$.playtimeMinutes", is(180)))
                .andExpect(jsonPath("$.headerImageUrl", is("https://cdn.example.com/cyberpunk.jpg")));

        // Verify repository data persistence
        List<LibraryItem> persistedItems = libraryRepository.findAll();
        assertEquals(1, persistedItems.size());
        assertEquals("Cyberpunk 2077", persistedItems.get(0).getTitle());
        assertEquals("PLAYING", persistedItems.get(0).getStatus());
    }

    @Test
    void getAllGames_shouldFetchFromRepositoryAndReturnValidJsonArray() throws Exception {
        // Arrange: seed real records via repository
        LibraryItem game1 = new LibraryItem(null, 620L, "Portal 2", "COMPLETED", 300, null);
        LibraryItem game2 = new LibraryItem(null, 105600L, "Terraria", "BACKLOG", 0, null);
        libraryRepository.saveAll(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/api/library")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Portal 2")))
                .andExpect(jsonPath("$[0].status", is("COMPLETED")))
                .andExpect(jsonPath("$[1].title", is("Terraria")))
                .andExpect(jsonPath("$[1].status", is("BACKLOG")));
    }

    @Test
    void getById_whenItemExistsInRepository_shouldReturnValidJson() throws Exception {
        // Arrange
        LibraryItem saved = libraryRepository.save(new LibraryItem(null, "Hades", "PLAYING"));

        // Act & Assert
        mockMvc.perform(get("/api/library/" + saved.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Hades")))
                .andExpect(jsonPath("$.status", is("PLAYING")));
    }

    @Test
    void addGame_whenTitleIsBlank_shouldReturnBadRequestAndNotPersist() throws Exception {
        // Arrange: invalid payload
        String invalidJson = "{\"title\": \"   \", \"status\": \"PLAYING\"}";

        // Act & Assert
        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify repository remains unchanged
        assertEquals(0, libraryRepository.count());
    }

    @Test
    void deleteById_shouldRemoveFromRepositoryAndReturnNoContent() throws Exception {
        // Arrange
        LibraryItem saved = libraryRepository.save(new LibraryItem(null, "Celeste", "COMPLETED"));
        Long id = saved.getId();
        assertTrue(libraryRepository.existsById(id));

        // Act & Assert
        mockMvc.perform(delete("/api/library/" + id))
                .andExpect(status().isNoContent());

        // Verify removed from repository
        assertFalse(libraryRepository.existsById(id));
    }
}
