package com.tourism.backend.repository;

import com.tourism.backend.entity.BranchContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchContactRepository extends JpaRepository<BranchContact, Integer> {
    boolean existsByBranchName(String branchName);

    boolean existsByBranchNameAndContactIDNot(String branchName, Integer contactID);

    boolean existsByIsHeadOfficeTrue();

    boolean existsByIsHeadOfficeTrueAndContactIDNot(Integer contactID);

    @Query("SELECT b FROM BranchContact b WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(b.branchName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(b.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(b.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BranchContact> searchBranches(@Param("keyword") String keyword, Pageable pageable);
}