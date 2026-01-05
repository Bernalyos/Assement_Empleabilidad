package com.codeup.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Response object for task details")
public record TaskResponse(
    @Schema(description = "Unique identifier of the task", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID id,
    @Schema(description = "Identifier of the project this task belongs to")
    UUID projectId,
    @Schema(description = "Title of the task", example = "Design Homepage")
    String title,
    @Schema(description = "Indicates if the task is completed")
    boolean completed,
    @Schema(description = "Indicates if the task has been soft-deleted")
    boolean deleted
) {}
