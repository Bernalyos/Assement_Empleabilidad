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
    void createProject_ShouldSaveAndReturnId() {
        String projectName = "New Project";
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = projectService.create(projectName);

        assertNotNull(resultId);
        verify(projectRepository).save(any(Project.class));
        verify(auditLogPort).register(eq("CREATE_PROJECT"), any(UUID.class));
    }

    @Test
    void activateProject_WhenProjectExistsAndHasTasks_ShouldSetStatusToActive() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        Task task = new Task(UUID.randomUUID(), projectId, "Test Task", false, false);
        when(taskRepository.findAll()).thenReturn(List.of(task));

        projectService.activate(projectId);

        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        verify(projectRepository).save(project);
        verify(auditLogPort).register(eq("ACTIVATE_PROJECT"), eq(projectId));
        verify(notificationPort).notify(contains("activated"));
    }

    @Test
    void activateProject_WhenProjectHasNoTasks_ShouldThrowBusinessRuleViolation() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, 
            () -> projectService.activate(projectId));
        
        assertTrue(exception.getMessage().contains("at least one task"));
        assertEquals(ProjectStatus.DRAFT, project.getStatus());
        verify(projectRepository, never()).save(project);
    }

    @Test
    void activateProject_WhenUserIsNotOwner_ShouldThrowUnauthorizedAction() {
        UUID otherUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(otherUserId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(UnauthorizedActionException.class, () -> projectService.activate(projectId));
        verify(projectRepository, never()).save(project);
    }

    @Test
    void deleteProject_WhenOwnerDeletes_ShouldMarkAsDeleted() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.delete(projectId);

        assertTrue(project.getDeleted());
        verify(projectRepository).save(project);
        verify(auditLogPort).register(eq("DELETE_PROJECT"), eq(projectId));
    }

    @Test
    void deleteProject_WhenProjectNotFound_ShouldThrowException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.delete(projectId));

        verify(projectRepository, never()).save(any());
    }
}
