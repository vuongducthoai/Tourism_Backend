package com.tourism.backend.repository;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.Role;
import com.tourism.backend.repository.custom.UserRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> , UserRepositoryCustom {
    Optional<User> findByUserID(Integer userID);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);


    // Thống kê tăng trưởng User theo ngày (lấy Role được truyền vào)
    @Query("SELECT new com.tourism.backend.dto.responseDTO.DashboardStatsDTO$DailyUserGrowth(" +
            "CAST(u.createdAt AS LocalDate), " +
            "COUNT(u), " +
            "0L) " +
            "FROM User u " +
            "WHERE u.role = :role " + // Thêm điều kiện Role
            "AND u.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(u.createdAt AS LocalDate) " +
            "ORDER BY CAST(u.createdAt AS LocalDate)")
    List<DashboardStatsDTO.DailyUserGrowth> getDailyNewUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("role") Role role
    );

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.createdAt < :date")
    Long countByRoleAndCreatedAtBefore(@Param("role") Role role, @Param("date") LocalDateTime date);

    Long countByRole(Role role);

    Long countByStatusAndRole(Boolean status, Role role);

    Long countByRoleAndCreatedAtBetween(Role role, LocalDateTime start, LocalDateTime end);

    List<User> findTop5ByRoleOrderByCreatedAtDesc(Role role);


}



