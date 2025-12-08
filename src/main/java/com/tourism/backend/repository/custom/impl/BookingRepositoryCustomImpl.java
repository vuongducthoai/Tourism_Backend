// src/main/java/com/tourism/backend/repository/custom/impl/BookingRepositoryCustomImpl.java

package com.tourism.backend.repository.custom.impl;

import com.tourism.backend.dto.requestDTO.BookingSearchRequestDTO;
import com.tourism.backend.entity.Booking;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.repository.custom.BookingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Booking> searchBookings(BookingSearchRequestDTO searchDTO, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. QUERY CHO NỘI DUNG (Content)
        CriteriaQuery<Booking> contentQuery = cb.createQuery(Booking.class);
        Root<Booking> bookingRoot = contentQuery.from(Booking.class);
        List<Predicate> predicates = buildPredicates(searchDTO, cb, bookingRoot);

        contentQuery.where(predicates.toArray(new Predicate[0]));

        // Áp dụng sắp xếp
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    contentQuery.orderBy(cb.asc(bookingRoot.get(order.getProperty())));
                } else {
                    contentQuery.orderBy(cb.desc(bookingRoot.get(order.getProperty())));
                }
            });
        }


        TypedQuery<Booking> typedQuery = entityManager.createQuery(contentQuery);

        // Áp dụng phân trang
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Booking> bookings = typedQuery.getResultList();

        // 2. QUERY CHO TỔNG SỐ PHẦN TỬ (Count)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Booking> countRoot = countQuery.from(Booking.class);
        List<Predicate> countPredicates = buildPredicates(searchDTO, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(bookings, pageable, total);
    }

    private List<Predicate> buildPredicates(BookingSearchRequestDTO searchDTO, CriteriaBuilder cb, Root<Booking> root) {
        List<Predicate> predicates = new ArrayList<>();

        // 1. Tìm kiếm theo bookingCode (LIKE)
        if (searchDTO.getBookingCode() != null && !searchDTO.getBookingCode().trim().isEmpty()) {
            String likePattern = "%" + searchDTO.getBookingCode().trim().toUpperCase() + "%";
            predicates.add(cb.like(cb.upper(root.get("bookingCode")), likePattern));
        }

        // 2. Tìm kiếm theo bookingStatus (EQUAL)
        if (searchDTO.getBookingStatus() != null && !searchDTO.getBookingStatus().trim().isEmpty()) {
            try {
                BookingStatus status = BookingStatus.valueOf(searchDTO.getBookingStatus().trim().toUpperCase());
                predicates.add(cb.equal(root.get("bookingStatus"), status));
            } catch (IllegalArgumentException e) {
                // Bỏ qua nếu status không hợp lệ
            }
        }

        // 3. Tìm kiếm theo bookingDate (BETWEEN)
        if (searchDTO.getBookingDate() != null) {
            // Chuỗi ISO (VD: 2025-02-14T00:00:00) được map thành LocalDateTime trong DTO
            LocalDateTime startOfDay = searchDTO.getBookingDate();
            LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            predicates.add(cb.between(root.get("bookingDate"), startOfDay, endOfDay));
        }

        return predicates;
    }
}