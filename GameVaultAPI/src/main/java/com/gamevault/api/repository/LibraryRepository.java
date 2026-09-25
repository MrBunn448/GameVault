package com.gamevault.api.repository;

import com.gamevault.api.model.LibraryItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LibraryRepository {

    private final List<LibraryItem> items = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public LibraryRepository() {
        save(new LibraryItem(null, "Hades", "PLAYING"));
        save(new LibraryItem(null, "Elden Ring", "COMPLETED"));
        save(new LibraryItem(null, "Hollow Knight", "BACKLOG"));
    }

    public List<LibraryItem> findAll() {
        return new ArrayList<>(items);
    }

    public Optional<LibraryItem> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst();
    }

    public LibraryItem save(LibraryItem item) {
        if (item.getId() == null) {
            item.setId(idSequence.getAndIncrement());
        }
        items.add(item);
        return item;
    }
}
