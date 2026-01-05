package com.codeup.domain.port.out;

import com.codeup.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findAll();
    List<Task> findByProjectId(UUID projectId);
    void deleteById(UUID id);
}
