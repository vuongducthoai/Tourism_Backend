package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.BookingPassengerResponseDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.BookingPassenger;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingConverter {

    private final ModelMapper modelMapper;

    public BookingResponseDTO convertToBookingResponseDTO(Booking booking) {
        // Map các trường từ Booking entity (sử dụng ModelMapper)
        BookingResponseDTO dto = modelMapper.map(booking, BookingResponseDTO.class);

        // Map thủ công các trường từ Booking entity (dòng 18-32 trong BookingResponseDTO)
        dto.setBookingID(booking.getBookingID());
        dto.setBookingCode(booking.getBookingCode());
        dto.setBookingDate(booking.getBookingDate());
        dto.setContactEmail(booking.getContactEmail());
        dto.setContactFullName(booking.getContactFullName());
        dto.setContactPhone(booking.getContactPhone());
        dto.setContactAddress(booking.getContactAddress());
        dto.setCustomerNote(booking.getCustomerNote());
        dto.setTotalPassengers(booking.getTotalPassengers());
        dto.setSurcharge(booking.getSurcharge());
        dto.setCouponDiscount(booking.getCouponDiscount());
        dto.setPaidByCoin(booking.getPaidByCoin());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setCancelReason(booking.getCancelReason());

        // Convert bookingStatus enum sang String
        if (booking.getBookingStatus() != null) {
            dto.setBookingStatus(booking.getBookingStatus().name());
        }

        // Set thông tin từ TourDeparture
        if (booking.getTourDeparture() != null) {
            dto.setDepartureID(booking.getTourDeparture().getDepartureID());
            dto.setDepartureDate(booking.getTourDeparture().getDepartureDate());
        }

        // Set thông tin từ Tour
        if (booking.getTourDeparture() != null && booking.getTourDeparture().getTour() != null) {
            var tour = booking.getTourDeparture().getTour();
            dto.setTourID(tour.getTourID());
            dto.setTourCode(tour.getTourCode());
            dto.setTourName(tour.getTourName());

            // Lấy ảnh đầu tiên từ list images của Tour
            String imageUrl = null;
            if (tour.getImages() != null && !tour.getImages().isEmpty()) {
                // Ưu tiên lấy ảnh chính (isMainImage = TRUE)
                Optional<String> mainImageOpt = tour.getImages().stream()
                        .filter(img -> img.getIsMainImage() != null && img.getIsMainImage())
                        .map(img -> img.getImageURL())
                        .findFirst();

                // Nếu có ảnh chính, dùng ảnh đó, nếu không, dùng ảnh đầu tiên trong list
                imageUrl = mainImageOpt.orElse(tour.getImages().get(0).getImageURL());
            }
            dto.setImage(imageUrl);
        }

        // Set thông tin từ Payment
        if (booking.getPayment() != null) {
            dto.setPaymentID(booking.getPayment().getPaymentID());
            dto.setAmount(booking.getPayment().getAmount());
            dto.setTimeLimit(booking.getPayment().getTimeLimit());
        }

        // Set list passengers từ BookingPassenger
        if (booking.getPassengers() != null && !booking.getPassengers().isEmpty()) {
            List<BookingPassengerResponseDTO> passengerDTOs = booking.getPassengers().stream()
                    .map(this::convertToBookingPassengerResponseDTO)
                    .collect(Collectors.toList());
            dto.setPassengers(passengerDTOs);
        }

        return dto;
    }

    private BookingPassengerResponseDTO convertToBookingPassengerResponseDTO(BookingPassenger passenger) {
        return modelMapper.map(passenger, BookingPassengerResponseDTO.class);
    }
}

