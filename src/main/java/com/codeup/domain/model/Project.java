package com.codeup.domain.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Project {

    private UUID id;
    private UUID ownerId;
    private String name;
    private ProjectStatus status;
    private boolean deleted;    


    public Project(UUID id, UUID ownerId, String name, ProjectStatus status, boolean deleted) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.status = status;
        this.deleted = deleted;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public ProjectStatus getStatus() { return status; }
    public boolean  getDeleted() { return deleted; }

    public void setId(UUID id) { this.id = id; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public void setName(String name) { this.name = name; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
