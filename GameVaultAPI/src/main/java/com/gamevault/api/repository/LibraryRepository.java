package com.gamevault.api.repository;

import com.gamevault.api.model.LibraryItem;

import java.util.List;
import java.util.Optional;

/**
 * Data access abstraction for personal library entries.
 * Enforces the Dependency Inversion Principle (DIP).
 */
public interface LibraryRepository {

    List<LibraryItem> findAll();

    Optional<LibraryItem> findById(Long id);

    LibraryItem save(LibraryItem item);

    boolean deleteById(Long id);
}
