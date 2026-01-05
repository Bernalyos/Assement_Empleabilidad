package com.codeup.domain.port.in;

import java.util.UUID;

public interface CreateProjectUseCase {
    UUID create(String name);
}
