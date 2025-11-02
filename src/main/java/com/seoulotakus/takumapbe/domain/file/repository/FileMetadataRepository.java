package com.seoulotakus.takumapbe.domain.file.repository;

import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByObjectKeyIn(List<String> objectKeys);

    /**
     * type과 typeId로 삭제되지 않은 파일 목록을 조회합니다.
     * (findByReview(review) 메서드 대체)
     * @param type "REVIEW", "SHOP"
     * @param typeId review.getId(), shop.getId()
     */
    List<FileMetadata> findByTypeAndTypeIdAndDeletedAtIsNull(String type, Long typeId);
}
