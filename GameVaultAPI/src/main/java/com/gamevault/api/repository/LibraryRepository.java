package com.gamevault.api.repository;

import com.gamevault.api.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Spring Data JPA repository for library persistence in MySQL
@Repository
public interface LibraryRepository extends JpaRepository<LibraryItem, Long> {

    // Helper to check if a Steam game is already saved in the library
    Optional<LibraryItem> findBySteamAppId(Long steamAppId);
}
