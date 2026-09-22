package com.gamevault.api.dto;

import com.gamevault.api.model.GameStatus;
import com.gamevault.api.model.LibraryItem;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a game entry returned to the client.
 */
public record LibraryItemResponse(
        Long id,
        String title,
        GameStatus status,
        Integer personalRating,
        Integer playtimeHours,
        LocalDate startDate,
        LocalDate completedDate
) {
    public static LibraryItemResponse from(LibraryItem item) {
        return new LibraryItemResponse(
                item.getId(),
                item.getTitle(),
                item.getStatus(),
                item.getPersonalRating(),
                item.getPlaytimeHours(),
                item.getStartDate(),
                item.getCompletedDate()
        );
    }
}
