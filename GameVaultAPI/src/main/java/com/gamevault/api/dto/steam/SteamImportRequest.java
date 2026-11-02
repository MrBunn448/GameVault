package com.gamevault.api.dto.steam;

// Request body DTO for importing games from Steam
public class SteamImportRequest {

    private String steamId;

    public SteamImportRequest() {
    }

    public SteamImportRequest(String steamId) {
        this.steamId = steamId;
    }

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }
}
