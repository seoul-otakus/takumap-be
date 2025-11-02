package com.seoulotakus.takumapbe.domain.file.entity;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_file_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "object_key")
    private String objectKey;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private UserEntity uploadedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "type_id")
    private Long typeId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 파일(나)을 특정 도메인(리뷰, 샵 등)과 연결합니다.
     * @param type "REVIEW", "SHOP" 등
     * @param typeId review.getId(), shop.getId() 등
     * @param user 업로드를 완료한 사용자
     */
    public void linkAssociation(String type, Long typeId, UserEntity user) {
        this.type = type;
        this.typeId = typeId;
        this.uploadedBy = user;
    }

    @Builder
    public FileMetadata(String fileName, String mimeType, String objectKey, UserEntity createdBy) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.objectKey = objectKey;
        this.createdBy = createdBy;
    }
}
