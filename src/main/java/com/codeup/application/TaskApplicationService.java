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
import java.util.UUID;

import com.codeup.domain.port.in.ListTasksUseCase;
import java.util.List;

/**
 * Application service that implements task-related use cases.
 * This service is framework-agnostic and follows the Hexagonal Architecture.
 */
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

    /**
     * Creates a new task for a specific project.
     * @param projectId The UUID of the project.
     * @param title The title of the task.
     * @return The UUID of the created task.
     * @throws BusinessRuleViolationException If the project is not in DRAFT status.
     */
    @Override
    public UUID create(UUID projectId, String title) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        if (project.getStatus() != com.codeup.domain.model.ProjectStatus.DRAFT) {
            throw new BusinessRuleViolationException("Tasks can only be added to projects in DRAFT status");
        }

        Task task = new Task(UUID.randomUUID(), projectId, title, false, false);
        taskRepository.save(task);
        auditLogPort.register("CREATE_TASK", task.getId());
        return task.getId();
    }

    /**
     * Marks a task as completed. Only the project owner can complete it.
     * @param taskId The UUID of the task to complete.
     * @throws BusinessRuleViolationException If the task is already completed.
     * @throws UnauthorizedActionException If the current user is not the owner.
     */
    @Override
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
    /**
     * Lists all tasks associated with a project.
     * @param projectId The UUID of the project.
     * @return A list of tasks.
     */
    @Override
    public List<Task> listByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    /**
     * Deletes a task (soft delete). Only the project owner can delete it.
     * @param taskId The UUID of the task to delete.
     * @throws UnauthorizedActionException If the current user is not the owner.
     */
    @Override
    public void delete(UUID taskId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        task.setDeleted(true);
        taskRepository.save(task);
        auditLogPort.register("DELETE_TASK", taskId);
    }
}
