package com.codeup.infrastructure.persistence.repository;

import com.codeup.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, UUID> {
}
