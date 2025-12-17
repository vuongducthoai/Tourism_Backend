package com.tourism.backend.repository;

import com.tourism.backend.entity.PolicyTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyTemplateRepository extends JpaRepository<PolicyTemplate, Integer> {

    boolean existsByTemplateName(String templateName);

    boolean existsByTemplateNameAndPolicyTemplateIDNot(String templateName, Integer id);

    List<PolicyTemplate> findByContact_ContactID(Integer contactId);

    @Query("SELECT p FROM PolicyTemplate p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(p.templateName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<PolicyTemplate> searchPolicies(@Param("keyword") String keyword, Pageable pageable);

    long countByContact_ContactID(Integer contactId);

    List<PolicyTemplate> findByStatusTrue();

    PolicyTemplate findByTemplateName(String templateName);
}