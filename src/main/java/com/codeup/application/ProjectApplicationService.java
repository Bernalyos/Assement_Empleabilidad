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

    @Override
    public UUID create(String name) {
        UUID ownerId = currentUserPort.getCurrentUserId();
        Project project = new Project(UUID.randomUUID(), ownerId, name, ProjectStatus.DRAFT, false);
        projectRepository.save(project);
        return project.getId();
    }

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
            throw new BusinessRuleViolationException("Project must have at least one active task to be activated");
        }

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);

        auditLogPort.register("ACTIVATE_PROJECT", projectId);
        notificationPort.notify("Project " + projectId + " activated");
    }

    @Override
    public void delete(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException();
        }

        projectRepository.deleteById(projectId);
        auditLogPort.register("DELETE_PROJECT", projectId);
    }
}
