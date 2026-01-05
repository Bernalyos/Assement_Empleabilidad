package com.codeup.infrastructure.configuration;

import com.codeup.application.ProjectApplicationService;
import com.codeup.application.TaskApplicationService;
import com.codeup.domain.port.out.AuditLogPort;
import com.codeup.domain.port.out.CurrentUserPort;
import com.codeup.domain.port.out.NotificationPort;
import com.codeup.domain.port.out.ProjectRepositoryPort;
import com.codeup.domain.port.out.TaskRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ProjectApplicationService projectApplicationService(
            ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new ProjectApplicationService(
                projectRepository,
                taskRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }

    @Bean
    public TaskApplicationService taskApplicationService(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            CurrentUserPort currentUserPort,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort) {
        return new TaskApplicationService(
                taskRepository,
                projectRepository,
                currentUserPort,
                auditLogPort,
                notificationPort
        );
    }
}
