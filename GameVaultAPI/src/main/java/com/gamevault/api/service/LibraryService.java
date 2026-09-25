package com.gamevault.api.service;

import com.gamevault.api.model.LibraryItem;
import com.gamevault.api.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final LibraryRepository repository;

    public LibraryService(LibraryRepository repository) {
        this.repository = repository;
    }

    public List<LibraryItem> getAll() {
        return repository.findAll();
    }

    public Optional<LibraryItem> getById(Long id) {
        return repository.findById(id);
    }

    public LibraryItem add(LibraryItem item) {
        if (item.getStatus() == null || item.getStatus().isBlank()) {
            item.setStatus("BACKLOG");
        }
        return repository.save(item);
    }
}
