package com.tourism.backend.repository.custom;

import com.tourism.backend.dto.requestDTO.BookingSearchRequestDTO;
import com.tourism.backend.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {
    Page<Booking> searchBookings(BookingSearchRequestDTO searchDTO, Pageable pageable);
}