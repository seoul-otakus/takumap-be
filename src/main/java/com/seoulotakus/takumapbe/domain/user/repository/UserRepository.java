package com.seoulotakus.takumapbe.domain.user.repository;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
            SELECT u
              FROM UserEntity u
             WHERE u.provider = :provider
              AND u.providerId = :providerId
            """)
    Optional<UserEntity> findByProviderAndProviderId(@Param("provider") Provider provider, @Param("providerId") String providerId);

    @Query("""
            SELECT u
              FROM UserEntity u
             WHERE u.userId = :userId
            """)
    Optional<UserEntity> findByUserId(@Param("userId") String userId);

    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE UserEntity u
               SET u.refreshToken = :refreshToken
             WHERE u.id = :id
            """)
    void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("id") Long id);

    @Query("""
        SELECT u
          FROM UserEntity u
         WHERE u.email = :email
    """)
    Optional<UserEntity> findIdByEmail(@Param("email") String email);

    @Query("""
        SELECT u
          FROM UserEntity u
         WHERE u.id = :userId
           AND u.email = :email
    """)
    Optional<UserEntity> findByUserIdAndEmail(@Param("userId") String userId, @Param("email")String email);
}
