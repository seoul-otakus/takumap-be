package com.seoulotakus.takumapbe.domain.file.repository;

import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByObjectKeyIn(List<String> objectKeys);

    List<FileMetadata> findByReview(Review review);
}
