package com.codeup.domain.port.out;

import com.codeup.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAll();
    List<Project> findByOwnerId(UUID ownerId);
    void deleteById(UUID id);
}
