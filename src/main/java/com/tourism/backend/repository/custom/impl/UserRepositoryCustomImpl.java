package com.tourism.backend.repository.custom.impl;

import com.tourism.backend.dto.requestDTO.UserSearchRequestDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.Role; // ✨ Import Enum Role
import com.tourism.backend.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> searchUsers(UserSearchRequestDTO searchDTO, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. Query lấy danh sách User
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // Xây dựng điều kiện
        List<Predicate> predicates = buildPredicates(cb, root, searchDTO);

        query.where(predicates.toArray(new Predicate[0]));

        // Sắp xếp
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    query.orderBy(cb.asc(root.get(order.getProperty())));
                } else {
                    query.orderBy(cb.desc(root.get(order.getProperty())));
                }
            });
        } else {
            query.orderBy(cb.desc(root.get("createdAt")));
        }

        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<User> users = typedQuery.getResultList();

        // 2. Query đếm tổng số bản ghi
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, searchDTO);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, pageable, totalElements);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<User> root, UserSearchRequestDTO searchDTO) {
        List<Predicate> predicates = new ArrayList<>();

        // ✨ MỚI: Chỉ lấy User có Role là CUSTOMER
        predicates.add(cb.equal(root.get("role"), Role.CUSTOMER));

        // 1. Tìm theo FullName (Ép kiểu String để tránh lỗi bytea nếu DB chưa chuẩn)
        if (searchDTO.getFullName() != null && !searchDTO.getFullName().trim().isEmpty()) {
            String nameValue = "%" + searchDTO.getFullName().trim().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("fullName").as(String.class)), nameValue));
        }

        // 2. Tìm theo Phone
        if (searchDTO.getPhone() != null && !searchDTO.getPhone().trim().isEmpty()) {
            String phoneValue = "%" + searchDTO.getPhone().trim() + "%";
            predicates.add(cb.like(root.get("phone").as(String.class), phoneValue));
        }

        // 3. Tìm theo Email
        if (searchDTO.getEmail() != null && !searchDTO.getEmail().trim().isEmpty()) {
            String emailValue = "%" + searchDTO.getEmail().trim().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("email").as(String.class)), emailValue));
        }

        return predicates;
    }
}