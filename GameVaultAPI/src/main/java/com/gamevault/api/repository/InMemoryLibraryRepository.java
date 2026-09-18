package com.gamevault.api.repository;

import com.gamevault.api.model.GameStatus;
import com.gamevault.api.model.LibraryItem;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * in-memory implementation of LibraryRepository for Sprint 1 / PR1.
 */
@Repository
public class InMemoryLibraryRepository implements LibraryRepository {

    private final Map<Long, LibraryItem> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @PostConstruct
    public void seedInitialData() {
        // Pre-populate with realistic gaming library records for evaluation testing
        save(new LibraryItem(
                null,
                "Hades",
                GameStatus.PLAYING,
                9,
                42,
                LocalDate.of(2026, 8, 15),
                null
        ));

        save(new LibraryItem(
                null,
                "Elden Ring",
                GameStatus.COMPLETED,
                10,
                118,
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 7, 2)
        ));

        save(new LibraryItem(
                null,
                "Hollow Knight",
                GameStatus.BACKLOG,
                null,
                0,
                null,
                null
        ));
    }

    @Override
    public List<LibraryItem> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<LibraryItem> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public LibraryItem save(LibraryItem item) {
        if (item.getId() == null) {
            item.setId(idSequence.getAndIncrement());
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }
}
