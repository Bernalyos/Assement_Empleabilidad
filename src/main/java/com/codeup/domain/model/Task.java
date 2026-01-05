package com.codeup.domain.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Task {
    
    private UUID id;
    private UUID projectId;
    private String title;
    private boolean completed;
    private boolean deleted;

    public Task(UUID id, UUID projectId, String title, boolean completed, boolean deleted) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.completed = completed;
        this.deleted = deleted;
    }



    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public boolean getCompleted() { return completed; }
    public boolean getDeleted() { return deleted; }

    public void setId(UUID id) { this.id = id; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public void setTitle(String title) { this.title = title; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
