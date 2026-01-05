package com.codeup.application;

import com.codeup.domain.exception.BusinessRuleViolationException;
import com.codeup.domain.exception.UnauthorizedActionException;
import com.codeup.domain.model.Project;
import com.codeup.domain.model.ProjectStatus;
import com.codeup.domain.model.Task;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.domain.port.out.TaskRepositoryPort;
import com.codeup.domain.port.in.ActivateProjectUseCase;
import com.codeup.domain.port.in.CreateProjectUseCase;
import com.codeup.domain.port.in.DeleteProjectUseCase;
import com.codeup.domain.port.out.AuditLogPort;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.NotificationPort;
import java.util.List;
import java.util.UUID;

/**
 * Application service that implements project-related use cases.
 * This service is framework-agnostic and follows the Hexagonal Architecture.
 */
public class ProjectApplicationService implements CreateProjectUseCase, ActivateProjectUseCase, DeleteProjectUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final TaskRepositoryPort taskRepository;
    private final CurrentUserPort currentUserPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    public ProjectApplicationService(ProjectRepositoryPort projectRepository, 
                                     TaskRepositoryPort taskRepository,
                                     CurrentUserPort currentUserPort,
                                     AuditLogPort auditLogPort,
                                     NotificationPort notificationPort) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.currentUserPort = currentUserPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    /**
     * Creates a new project in DRAFT status for the current user.
     * @param name The name of the project.
     * @return The UUID of the created project.
     */
    @Override
    public UUID create(String name) {
        UUID ownerId = currentUserPort.getCurrentUserId();
        Project project = new Project(UUID.randomUUID(), ownerId, name, ProjectStatus.DRAFT, false);
        projectRepository.save(project);
        auditLogPort.register("CREATE_PROJECT", project.getId());
        return project.getId();
    }

    /**
     * Activates a project. A project can only be activated if it has at least one task.
     * Only the project owner can activate it.
     * @param projectId The UUID of the project to activate.
     * @throws BusinessRuleViolationException If the project has no tasks.
     * @throws UnauthorizedActionException If the current user is not the owner.
     */
    @Override
    public void activate(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        List<Task> tasks = taskRepository.findAll(); 
        boolean hasActiveTasks = tasks.stream()
                .anyMatch(t -> t.getProjectId().equals(projectId) && !t.getDeleted());

        if (!hasActiveTasks) {
            throw new BusinessRuleViolationException("Project must have at least one task to be activated");
        }

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);

        auditLogPort.register("ACTIVATE_PROJECT", projectId);
        notificationPort.notify("Project " + projectId + " activated");
    }

    /**
     * Deletes a project (soft delete). Only the project owner can delete it.
     * @param projectId The UUID of the project to delete.
     * @throws UnauthorizedActionException If the current user is not the owner.
     */
    @Override
    public void delete(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        project.setDeleted(true);
        projectRepository.save(project);
        auditLogPort.register("DELETE_PROJECT", projectId);
    }
}
