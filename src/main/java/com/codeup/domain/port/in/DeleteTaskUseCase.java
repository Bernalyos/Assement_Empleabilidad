package com.codeup.domain.port.in;

import java.util.UUID;

public interface DeleteTaskUseCase {
    void delete(UUID id);
}
