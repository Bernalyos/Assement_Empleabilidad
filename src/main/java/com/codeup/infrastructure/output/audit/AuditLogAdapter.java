package com.codeup.infrastructure.output.audit;

import com.codeup.domain.port.out.AuditLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditLogAdapter implements AuditLogPort {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAdapter.class);

    @Override
    public void register(String action, UUID entityId) {
        logger.info("AUDIT LOG: Action='{}', EntityId='{}'", action, entityId);
    }
}
