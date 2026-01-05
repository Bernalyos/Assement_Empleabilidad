package com.codeup.application;

import com.codeup.domain.exception.BusinessRuleViolationException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskApplicationServiceTest {

    @Mock
    private TaskRepositoryPort taskRepository;
    @Mock
    private ProjectRepositoryPort projectRepository;
    @Mock
    private CurrentUserPort currentUserPort;
    @Mock
    private AuditLogPort auditLogPort;
    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private TaskApplicationService taskService;

    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        project = new Project(projectId, userId, "Test Project", ProjectStatus.ACTIVE, false);
        task = new Task(taskId, projectId, "Test Task", false, false);
    }

    @Test
    void createTask_WhenProjectIsDraft_ShouldSaveAndReturnId() {
        String taskTitle = "New Task";
        project.setStatus(ProjectStatus.DRAFT);
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = taskService.create(projectId, taskTitle);

        assertNotNull(resultId);
        verify(taskRepository).save(any(Task.class));
        verify(auditLogPort).register(eq("CREATE_TASK"), any(UUID.class));
    }

    @Test
    void createTask_WhenProjectIsNotDraft_ShouldThrowBusinessRuleViolation() {
        project.setStatus(ProjectStatus.ACTIVE);
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(BusinessRuleViolationException.class, () -> taskService.create(projectId, "Title"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void completeTask_WhenOwnerCompletes_ShouldMarkAsCompleted() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        taskService.complete(taskId);

        assertTrue(task.getCompleted());
        verify(taskRepository).save(task);
        verify(auditLogPort).register(eq("COMPLETE_TASK"), eq(taskId));
        verify(notificationPort).notify(contains("completed"));
    }

    @Test
    void completeTask_WhenAlreadyCompleted_ShouldThrowBusinessRuleViolation() {
        task.setCompleted(true);
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(BusinessRuleViolationException.class, () -> taskService.complete(taskId));
        verify(taskRepository, never()).save(task);
    }

    @Test
    void deleteTask_WhenOwnerDeletes_ShouldMarkAsDeleted() {
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        taskService.delete(taskId);

        assertTrue(task.getDeleted());
        verify(taskRepository).save(task);
        verify(auditLogPort).register(eq("DELETE_TASK"), eq(taskId));
    }
}
