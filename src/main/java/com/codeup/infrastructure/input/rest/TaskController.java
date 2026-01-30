package com.codeup.infrastructure.input.rest;

import com.codeup.domain.port.in.CompleteTaskUseCase;
import com.codeup.domain.port.in.CreateTaskUseCase;
import com.codeup.domain.port.in.DeleteTaskUseCase;
import com.codeup.domain.port.in.ListTasksUseCase;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.infrastructure.input.rest.dto.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tasks", description = "Operations related to task management")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public TaskController(CreateTaskUseCase createTaskUseCase, 
                          CompleteTaskUseCase completeTaskUseCase,
                          ListTasksUseCase listTasksUseCase,
                          DeleteTaskUseCase deleteTaskUseCase,
                          ProjectRepositoryPort projectRepositoryPort,
                          CurrentUserPort currentUserPort) {
        this.createTaskUseCase = createTaskUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(summary = "Create a new task", description = "Creates a task for a specific project.")
    public ResponseEntity<UUID> createTask(@PathVariable UUID projectId, @RequestBody CreateTaskRequest request) {
        UUID taskId = createTaskUseCase.create(projectId, request.title());
        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(summary = "List tasks by project", description = "Retrieves all tasks associated with a project for the owner.")
    public ResponseEntity<List<TaskResponse>> listTasks(@PathVariable UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        return projectRepositoryPort.findById(projectId)
                .map(project -> {
                    if (!project.getOwnerId().equals(currentUserId)) {
                        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).<List<TaskResponse>>build();
                    }
                    List<TaskResponse> response = listTasksUseCase.listByProjectId(projectId).stream()
                            .map(t -> new TaskResponse(t.getId(), t.getProjectId(), t.getTitle(), t.getCompleted(), t.getDeleted()))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/tasks/{id}/complete")
    @Operation(summary = "Complete a task", description = "Marks a task as completed. Only the project owner can perform this.")
    public ResponseEntity<Void> completeTask(@PathVariable UUID id) {
        completeTaskUseCase.complete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete a task", description = "Performs a soft delete on the task.")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        deleteTaskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateTaskRequest(String title) {}
}
