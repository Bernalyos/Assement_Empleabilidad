package com.codeup.infrastructure.input.rest;

import com.codeup.domain.port.in.CompleteTaskUseCase;
import com.codeup.domain.port.in.CreateTaskUseCase;
import com.codeup.domain.port.in.DeleteTaskUseCase;
import com.codeup.domain.port.in.ListTasksUseCase;
import com.codeup.infrastructure.input.rest.dto.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase, 
                          CompleteTaskUseCase completeTaskUseCase,
                          ListTasksUseCase listTasksUseCase,
                          DeleteTaskUseCase deleteTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<UUID> createTask(@PathVariable UUID projectId, @RequestBody CreateTaskRequest request) {
        UUID taskId = createTaskUseCase.create(projectId, request.title());
        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks(@PathVariable UUID projectId) {
        List<TaskResponse> response = listTasksUseCase.listByProjectId(projectId).stream()
                .map(t -> new TaskResponse(t.getId(), t.getProjectId(), t.getTitle(), t.getCompleted(), t.getDeleted()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable UUID id) {
        completeTaskUseCase.complete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        deleteTaskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateTaskRequest(String title) {}
}
