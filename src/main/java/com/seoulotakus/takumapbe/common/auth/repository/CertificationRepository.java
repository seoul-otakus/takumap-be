package com.seoulotakus.takumapbe.common.auth.repository;

import com.seoulotakus.takumapbe.common.auth.entity.CertificationEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificationRepository extends JpaRepository<CertificationEntity, Long> {

    @Query("""
            SELECT c
              FROM CertificationEntity c
             WHERE c.userId = :userId
            """)
    CertificationEntity findByUserId(@Param("userId") String userId);

    @Transactional
    void deleteByUserId(@NotBlank String userId);
}
