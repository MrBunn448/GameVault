package com.gamevault.api.repository;

import com.gamevault.api.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryItem, Long> {
    // This tells Spring Data JPA that the repository stores LibraryItem entities whose IDs are Longs.
    // Spring provides common methods such as findAll, findById etc.

    //  check if a Steam game is already saved in the library
    Optional<LibraryItem> findBySteamAppId(Long steamAppId);
}
