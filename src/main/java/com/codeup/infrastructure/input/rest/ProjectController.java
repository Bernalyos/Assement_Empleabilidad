package com.codeup.infrastructure.input.rest;

import com.codeup.domain.port.in.ActivateProjectUseCase;
import com.codeup.domain.port.in.CreateProjectUseCase;
import com.codeup.domain.port.in.DeleteProjectUseCase;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.infrastructure.input.rest.dto.ProjectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ActivateProjectUseCase activateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final ProjectRepositoryPort projectRepositoryPort;

    public ProjectController(CreateProjectUseCase createProjectUseCase,
                             ActivateProjectUseCase activateProjectUseCase,
                             DeleteProjectUseCase deleteProjectUseCase,
                             ProjectRepositoryPort projectRepositoryPort) {
        this.createProjectUseCase = createProjectUseCase;
        this.activateProjectUseCase = activateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @PostMapping
    public ResponseEntity<UUID> createProject(@RequestBody CreateProjectRequest request) {
        UUID projectId = createProjectUseCase.create(request.name());
        return ResponseEntity.ok(projectId);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        List<ProjectResponse> response = projectRepositoryPort.findAll().stream()
                .map(p -> new ProjectResponse(p.getId(), p.getOwnerId(), p.getName(), p.getStatus(), p.getDeleted()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProject(@PathVariable UUID id) {
        activateProjectUseCase.activate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        deleteProjectUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateProjectRequest(String name) {}
}
