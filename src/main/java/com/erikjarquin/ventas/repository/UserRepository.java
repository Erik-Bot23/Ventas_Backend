package com.erikjarquin.ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erikjarquin.ventas.model.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("""
        SELECT DISTINCT u
            FROM UserEntity u
                JOIN FETCH u.role r
                JOIN FETCH r.permissions
                    WHERE u.email = :email
        """)
    
    Optional<UserEntity> findByEmailWithRoleAndPermissions(@Param("email") String email);
    Optional<UserEntity> findByResetToken(String resetToken);
    
}
