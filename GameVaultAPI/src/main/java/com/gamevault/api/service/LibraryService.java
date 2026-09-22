package com.gamevault.api.service;

import com.gamevault.api.dto.CreateLibraryItemRequest;
import com.gamevault.api.dto.LibraryItemResponse;

import java.util.List;

/**
 * Service interface defining the business operations for user game libraries.
 * Enforces the Dependency Inversion Principle (DIP).
 */
public interface LibraryService {

    List<LibraryItemResponse> getAllLibraryItems();

    LibraryItemResponse getLibraryItemById(Long id);

    LibraryItemResponse addGameToLibrary(CreateLibraryItemRequest request);

    void removeGameFromLibrary(Long id);
}
