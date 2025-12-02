// File: com.tourism.backend.repository.RefundInformationRepository.java
package com.tourism.backend.repository;

import com.tourism.backend.entity.RefundInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundInformationRepository extends JpaRepository<RefundInformation, Integer> {
    // Không cần thêm phương thức đặc biệt, JpaRepository đã cung cấp đủ.
}