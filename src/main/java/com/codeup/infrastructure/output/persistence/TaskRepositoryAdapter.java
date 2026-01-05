package com.codeup.infrastructure.output.persistence;

import com.codeup.domain.model.Task;
import com.codeup.domain.port.out.TaskRepositoryPort;
import com.codeup.infrastructure.persistence.entity.TaskEntity;
import com.codeup.infrastructure.persistence.repository.JpaTaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final JpaTaskRepository jpaTaskRepository;

    public TaskRepositoryAdapter(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        TaskEntity savedEntity = jpaTaskRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaTaskRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return jpaTaskRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return jpaTaskRepository.findByProjectId(projectId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaTaskRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            jpaTaskRepository.save(entity);
        });
    }

    private TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.getCompleted(),
                task.getDeleted()
        );
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.isDeleted()
        );
    }
}
