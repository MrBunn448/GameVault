package com.gamevault.api.dto.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Holds individual game data returned from Steam Web API
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamGameDto {

    @JsonProperty("appid")
    private Long appId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("playtime_forever")
    private Integer playtimeForever;

    @JsonProperty("img_icon_url")
    private String imgIconUrl;

    public SteamGameDto() {
    }

    public SteamGameDto(Long appId, String name, Integer playtimeForever, String imgIconUrl) {
        this.appId = appId;
        this.name = name;
        this.playtimeForever = playtimeForever;
        this.imgIconUrl = imgIconUrl;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlaytimeForever() {
        return playtimeForever;
    }

    public void setPlaytimeForever(Integer playtimeForever) {
        this.playtimeForever = playtimeForever;
    }

    public String getImgIconUrl() {
        return imgIconUrl;
    }

    public void setImgIconUrl(String imgIconUrl) {
        this.imgIconUrl = imgIconUrl;
    }

    // Helper returning Steam CDN header image URL
    public String getHeaderImageUrl() {
        if (appId == null) {
            return null;
        }
        return "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/" + appId + "/header.jpg";
    }
}
