package com.codeup.infrastructure.input.rest.dto;

import com.codeup.domain.model.ProjectStatus;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    UUID ownerId,
    String name,
    ProjectStatus status,
    boolean deleted
) {}
