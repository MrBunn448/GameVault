package com.gamevault.api.client;

import com.gamevault.api.dto.steam.SteamGameDto;
import com.gamevault.api.dto.steam.SteamResponseWrapper;
import com.gamevault.api.dto.steam.SteamVanityResponseWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;

// Client communicating directly with Valve's Steamworks Web API
@Component // defines this for spring to inject into other classes
public class SteamApiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public SteamApiClient(
            @Value("${steam.api.key:}") String apiKey,
            @Value("${steam.api.base-url:https://api.steampowered.com}") String baseUrl) {
        this.restClient = RestClient.create();
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.baseUrl = baseUrl != null ? baseUrl.trim() : "https://api.steampowered.com";
    }

    // Fetches owned games using either a numeric Steam ID or a Steam profile URL.
    // Profile URLs may contain a numeric ID (/profiles/...) or a custom name (/id/...).
    public List<SteamGameDto> getOwnedGames(String rawInput) {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Steam API key is not set. Please configure the STEAM_API_KEY environment variable.");
        }
        if (rawInput == null || rawInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Steam ID cannot be empty.");
        }

        String resolvedId = resolveTo64BitSteamId(rawInput.trim());

        String url = String.format(
                "%s/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
                baseUrl, apiKey, resolvedId
        );

        try {
            SteamResponseWrapper response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(SteamResponseWrapper.class);

            if (response != null && response.getResponse() != null && response.getResponse().getGames() != null) {
                return response.getResponse().getGames();
            }

            return Collections.emptyList();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 400 || ex.getStatusCode().value() == 403) {
                throw new IllegalArgumentException(
                        "Unable to fetch Steam games. Please check that your 17-digit Steam ID is correct and that your profile Game Details are set to Public in Steam Privacy Settings."
                );
            }
            throw new RuntimeException("Steam API error (" + ex.getStatusCode().value() + "): " + ex.getStatusText(), ex);
        }
    }

    // Normalizes either a numeric Steam ID or a profile URL into the numeric ID Steam's games endpoint expects.
    // For custom /id/ profile URLs, it asks Steam to resolve the profile name first.
    private String resolveTo64BitSteamId(String input) {
        String cleaned = input.trim();

        // Strip trailing slash if present
        if (cleaned.endsWith("/")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        // If user pasted a full URL
        if (cleaned.contains("/profiles/")) {
            cleaned = cleaned.substring(cleaned.lastIndexOf("/profiles/") + 10);
        } else if (cleaned.contains("/id/")) {
            cleaned = cleaned.substring(cleaned.lastIndexOf("/id/") + 4);
        }

        // If it's already a 17-digit numeric SteamID (starting with 7656...)
        if (cleaned.matches("^7656\\d{13}$")) {
            return cleaned;
        }

        // Otherwise try resolving as custom vanity URL name via Steam API
        try {
            String vanityUrl = String.format(
                    "%s/ISteamUser/ResolveVanityURL/v0001/?key=%s&vanityurl=%s",
                    baseUrl, apiKey, cleaned
            );

            SteamVanityResponseWrapper vanityResponse = restClient.get()
                    .uri(vanityUrl)
                    .retrieve()
                    .body(SteamVanityResponseWrapper.class);

            if (vanityResponse != null && vanityResponse.getResponse() != null) {
                if (Integer.valueOf(1).equals(vanityResponse.getResponse().getSuccess()) &&
                        vanityResponse.getResponse().getSteamId() != null) {
                    return vanityResponse.getResponse().getSteamId();
                }
            }
        } catch (Exception ignored) {
            // If resolution fails, proceed with cleaned input and let Steam validate
        }

        return cleaned;
    }
}
