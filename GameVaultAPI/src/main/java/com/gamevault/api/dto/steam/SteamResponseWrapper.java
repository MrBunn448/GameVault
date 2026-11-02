package com.gamevault.api.dto.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

// Wrapper matching Steam Web API outer JSON format
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamResponseWrapper {

    @JsonProperty("response")
    private SteamGamesContainer response;

    public SteamGamesContainer getResponse() {
        return response;
    }

    public void setResponse(SteamGamesContainer response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SteamGamesContainer {

        @JsonProperty("game_count")
        private Integer gameCount;

        @JsonProperty("games")
        private List<SteamGameDto> games;

        public Integer getGameCount() {
            return gameCount;
        }

        public void setGameCount(Integer gameCount) {
            this.gameCount = gameCount;
        }

        public List<SteamGameDto> getGames() {
            return games != null ? games : Collections.emptyList();
        }

        public void setGames(List<SteamGameDto> games) {
            this.games = games;
        }
    }
}
