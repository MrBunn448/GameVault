package com.gamevault.api.service;

import com.gamevault.api.dto.CreateLibraryItemRequest;
import com.gamevault.api.dto.LibraryItemResponse;
import com.gamevault.api.exception.ResourceNotFoundException;
import com.gamevault.api.model.GameStatus;
import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Concrete business implementation for library operations.
 * Communicates exclusively through the LibraryRepository interface (DIP).
 */
@Service
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository repository;

    public LibraryServiceImpl(LibraryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LibraryItemResponse> getAllLibraryItems() {
        return repository.findAll()
                .stream()
                .map(LibraryItemResponse::from)
                .toList();
    }

    @Override
    public LibraryItemResponse getLibraryItemById(Long id) {
        return repository.findById(id)
                .map(LibraryItemResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Library entry with id " + id + " not found."));
    }

    @Override
    public LibraryItemResponse addGameToLibrary(CreateLibraryItemRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate completedDate = request.getCompletedDate();

        // Business rule: if marked completed without an explicit completion date, default to today
        if (request.getStatus() == GameStatus.COMPLETED && completedDate == null) {
            completedDate = LocalDate.now();
        }

        // Business rule: if marked playing without an explicit start date, default to today
        if (request.getStatus() == GameStatus.PLAYING && startDate == null) {
            startDate = LocalDate.now();
        }

        LibraryItem newItem = new LibraryItem(
                null,
                request.getTitle().trim(),
                request.getStatus(),
                request.getPersonalRating(),
                request.getPlaytimeHours() != null ? request.getPlaytimeHours() : 0,
                startDate,
                completedDate
        );

        LibraryItem savedItem = repository.save(newItem);
        return LibraryItemResponse.from(savedItem);
    }

    @Override
    public void removeGameFromLibrary(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Cannot remove game: Library entry with id " + id + " not found.");
        }
    }
}
