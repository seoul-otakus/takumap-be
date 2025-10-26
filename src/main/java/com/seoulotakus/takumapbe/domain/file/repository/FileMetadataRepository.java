package com.seoulotakus.takumapbe.domain.file.repository;

import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
