package com.codeup.application;

import com.codeup.domain.exception.BusinessRuleViolationException;
import com.codeup.domain.exception.UnauthorizedActionException;
import com.codeup.domain.model.Project;
import com.codeup.domain.model.ProjectStatus;
import com.codeup.domain.model.Task;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.domain.port.out.TaskRepositoryPort;
import com.codeup.domain.port.out.AuditLogPort;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectApplicationServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepository;
    @Mock
    private TaskRepositoryPort taskRepository;
    @Mock
    private CurrentUserPort currentUserPort;
    @Mock
    private AuditLogPort auditLogPort;
    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private ProjectApplicationService projectService;

    private UUID userId;
    private UUID projectId;
    private Project project;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        project = new Project(projectId, userId, "Test Project", ProjectStatus.DRAFT, false);
    }

    @Test
    void ActivateProject_WithTasks_ShouldSucceed() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        Task task = new Task(UUID.randomUUID(), projectId, "Test Task", false, false);
        when(taskRepository.findAll()).thenReturn(List.of(task)); // Ideally should be findByProjectId

        projectService.activate(projectId);

        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        verify(projectRepository).save(project);
        verify(auditLogPort).register(eq("ACTIVATE_PROJECT"), eq(projectId));
        verify(notificationPort).notify(anyString());
    }

    @Test
    void ActivateProject_WithoutTasks_ShouldFail() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(BusinessRuleViolationException.class, () -> projectService.activate(projectId));
        
        assertEquals(ProjectStatus.DRAFT, project.getStatus());
        verify(projectRepository, never()).save(project);
    }

    @Test
    void ActivateProject_ByNonOwner_ShouldFail() {
        UUID otherUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(otherUserId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(UnauthorizedActionException.class, () -> projectService.activate(projectId));
        
        verify(projectRepository, never()).save(project);
    }
}
