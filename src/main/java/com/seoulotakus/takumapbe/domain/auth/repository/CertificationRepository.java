package com.seoulotakus.takumapbe.domain.auth.repository;

import com.seoulotakus.takumapbe.domain.auth.entity.CertificationEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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
