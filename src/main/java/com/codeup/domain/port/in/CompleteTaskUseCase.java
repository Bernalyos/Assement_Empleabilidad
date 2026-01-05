package com.codeup.domain.port.in;

import java.util.UUID;

public interface CompleteTaskUseCase {
    void complete(UUID taskId);
}
