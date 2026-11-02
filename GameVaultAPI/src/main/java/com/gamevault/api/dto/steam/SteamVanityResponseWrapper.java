package com.gamevault.api.dto.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// DTO for Valve's ResolveVanityURL API response
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamVanityResponseWrapper {

    @JsonProperty("response")
    private VanityContainer response;

    public VanityContainer getResponse() {
        return response;
    }

    public void setResponse(VanityContainer response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VanityContainer {
        @JsonProperty("steamid")
        private String steamId;

        @JsonProperty("success")
        private Integer success;

        public String getSteamId() {
            return steamId;
        }

        public void setSteamId(String steamId) {
            this.steamId = steamId;
        }

        public Integer getSuccess() {
            return success;
        }

        public void setSuccess(Integer success) {
            this.success = success;
        }
    }
}
