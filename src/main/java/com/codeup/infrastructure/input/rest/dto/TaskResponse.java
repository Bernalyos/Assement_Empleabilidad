package com.codeup.infrastructure.input.rest.dto;

import java.util.UUID;

public record TaskResponse(
    UUID id,
    UUID projectId,
    String title,
    boolean completed,
    boolean deleted
) {}
