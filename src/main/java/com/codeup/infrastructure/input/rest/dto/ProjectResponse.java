package com.codeup.infrastructure.input.rest.dto;

import com.codeup.domain.model.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Response object for project details")
public record ProjectResponse(
    @Schema(description = "Unique identifier of the project", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID id,
    @Schema(description = "Identifier of the user who owns the project")
    UUID ownerId,
    @Schema(description = "Name of the project", example = "Website Redesign")
    String name,
    @Schema(description = "Current status of the project")
    ProjectStatus status,
    @Schema(description = "Indicates if the project has been soft-deleted")
    boolean deleted
) {}
