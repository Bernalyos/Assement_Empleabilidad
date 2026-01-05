package com.codeup.application;

import com.codeup.domain.exception.BusinessRuleViolationException;
import com.codeup.domain.exception.UnauthorizedActionException;
import com.codeup.domain.model.Project;
import com.codeup.domain.model.Task;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.domain.port.out.TaskRepositoryPort;
import com.codeup.domain.port.in.CompleteTaskUseCase;
import com.codeup.domain.port.in.CreateTaskUseCase;
import com.codeup.domain.port.in.DeleteTaskUseCase;
import com.codeup.domain.port.out.AuditLogPort;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.NotificationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import com.codeup.domain.port.in.ListTasksUseCase;
import java.util.List;

@Service
public class TaskApplicationService implements CreateTaskUseCase, CompleteTaskUseCase, ListTasksUseCase, DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public TaskApplicationService(TaskRepositoryPort taskRepository,
                                  ProjectRepositoryPort projectRepository,
                                  CurrentUserPort currentUserPort,
                                  AuditLogPort auditLogPort,
                                  NotificationPort notificationPort) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUserPort = currentUserPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    @Transactional
    public UUID create(UUID projectId, String title) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        Task task = new Task(UUID.randomUUID(), projectId, title, false, false);
        taskRepository.save(task);
        return task.getId();
    }

    @Override
    @Transactional
    public void complete(UUID taskId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        if (task.getCompleted()) {
            throw new BusinessRuleViolationException("Task is already completed");
        }

        task.setCompleted(true);
        taskRepository.save(task);

        auditLogPort.register("COMPLETE_TASK", taskId);
        notificationPort.notify("Task " + taskId + " completed");
    }
    @Override
    public List<Task> listByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public void delete(UUID taskId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        taskRepository.deleteById(taskId);
        auditLogPort.register("DELETE_TASK", taskId);
    }
}
