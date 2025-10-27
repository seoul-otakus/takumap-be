package com.seoulotakus.takumapbe.domain.file.entity;

import com.seoulotakus.takumapbe.domain.review.entity.Review;
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

    // TODO. shop_id 연결?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Review review;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void linkToReview(Review review, UserEntity user) {
        this.type = "REVIEW";
        this.uploadedBy = user;
        this.review = review;
    }

    @Builder
    public FileMetadata(String fileName, String mimeType, String objectKey, UserEntity createdBy) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.objectKey = objectKey;
        this.createdBy = createdBy;
    }
}
