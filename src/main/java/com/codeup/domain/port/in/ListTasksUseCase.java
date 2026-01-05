package com.codeup.domain.port.in;

import com.codeup.domain.model.Task;
import java.util.List;
import java.util.UUID;

public interface ListTasksUseCase {
    List<Task> listByProjectId(UUID projectId);
}
