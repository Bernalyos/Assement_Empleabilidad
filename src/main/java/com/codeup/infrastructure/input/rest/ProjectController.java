package com.codeup.infrastructure.input.rest;

import com.codeup.domain.port.in.ActivateProjectUseCase;
import com.codeup.domain.port.in.CreateProjectUseCase;
import com.codeup.domain.port.in.DeleteProjectUseCase;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.infrastructure.input.rest.dto.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Operations related to project management")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ActivateProjectUseCase activateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public ProjectController(CreateProjectUseCase createProjectUseCase,
                             ActivateProjectUseCase activateProjectUseCase,
                             DeleteProjectUseCase deleteProjectUseCase,
                             ProjectRepositoryPort projectRepositoryPort,
                             CurrentUserPort currentUserPort) {
        this.createProjectUseCase = createProjectUseCase;
        this.activateProjectUseCase = activateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Creates a project in DRAFT status for the current user.")
    public ResponseEntity<UUID> createProject(@RequestBody CreateProjectRequest request) {
        UUID projectId = createProjectUseCase.create(request.name());
        return ResponseEntity.ok(projectId);
    }

    @GetMapping
    @Operation(summary = "List all projects", description = "Retrieves a list of all projects for the current user.")
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        UUID currentUserId = currentUserPort.getCurrentUserId();
        List<ProjectResponse> response = projectRepositoryPort.findByOwnerId(currentUserId).stream()
                .map(p -> new ProjectResponse(p.getId(), p.getOwnerId(), p.getName(), p.getStatus(), p.getDeleted()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a project", description = "Changes project status to ACTIVE. Requires at least one active task.")
    public ResponseEntity<Void> activateProject(@PathVariable UUID id) {
        activateProjectUseCase.activate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project", description = "Performs a soft delete on the project.")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        deleteProjectUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateProjectRequest(String name) {}
}
