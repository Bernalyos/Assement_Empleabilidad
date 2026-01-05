package com.codeup.domain.port.in;

import java.util.UUID;

public interface CreateTaskUseCase {
    UUID create(UUID projectId, String title);
}
