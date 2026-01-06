package com.codeup.infrastructure.output.persistence;

import com.codeup.domain.model.Project;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.infrastructure.persistence.entity.ProjectEntity;
import com.codeup.infrastructure.persistence.repository.JpaProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final JpaProjectRepository jpaProjectRepository;

    public ProjectRepositoryAdapter(JpaProjectRepository jpaProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        ProjectEntity savedEntity = jpaProjectRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaProjectRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return jpaProjectRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByOwnerId(UUID ownerId) {
        return jpaProjectRepository.findByOwnerId(ownerId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaProjectRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            jpaProjectRepository.save(entity);
        });
    }

    private ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId(),
                project.getOwnerId(),
                project.getName(),
                project.getStatus(),
                project.getDeleted()
        );
    }

    private Project toDomain(ProjectEntity entity) {
        return new Project(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getStatus(),
                entity.isDeleted()
        );
    }
}
