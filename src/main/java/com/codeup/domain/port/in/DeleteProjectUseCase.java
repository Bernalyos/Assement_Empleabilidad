package com.codeup.domain.port.in;

import java.util.UUID;

public interface DeleteProjectUseCase {
    void delete(UUID id);
}
