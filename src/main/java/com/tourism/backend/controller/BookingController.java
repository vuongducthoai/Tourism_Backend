package com.tourism.backend.controller;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingSearchRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingUpdateStatusRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO;
import com.tourism.backend.dto.requestDTO.RefundInformationRequestDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.domain.Sort;
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/order")
    public ResponseEntity<TourBookingInfoDTO> getBookingInitInfo(
            @RequestParam String tourCode,
            @RequestParam(required = false) Integer departureId
    ) {
        TourBookingInfoDTO response = bookingService.getTourBookingInfo(tourCode, departureId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<BookingDetailResponseDTO> createBooking(@RequestBody BookingRequestDTO request) {
        BookingDetailResponseDTO response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{bookingCode}")
    public ResponseEntity<BookingDetailResponseDTO> getBookingDetail(
            @PathVariable String bookingCode
    ) {
        BookingDetailResponseDTO response = bookingService.getBookingDetail(bookingCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<?> getAllBookingsByUser(
            @PathVariable Integer userID,
            @RequestParam(required = false) String bookingStatus
    ) {
        BookingStatus status = null;
        if (bookingStatus != null && !bookingStatus.trim().isEmpty()) {
            try {
                status = BookingStatus.valueOf(bookingStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                ErrorResponseDTO error = new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "Invalid booking status: " + bookingStatus
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }

        List<BookingResponseDTO> bookings = bookingService.getAllBookingsByUser(userID, status);
        return ResponseEntity.ok(bookings);
    }
      
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelBooking(
            @Valid @RequestBody BookingCancellationRequestDTO requestDTO
    ) {
        try {
            BookingResponseDTO cancelledBooking = bookingService.cancelBooking(requestDTO);
            return ResponseEntity.ok(cancelledBooking);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Cancellation Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi hủy booking: " + e.getMessage()
            );
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @PostMapping("/refund-request/{bookingID}")
    public ResponseEntity<?> requestRefund(
            @PathVariable Integer bookingID,
            @Valid @RequestBody RefundInformationRequestDTO refundDTO
    ) {
        try {
            BookingResponseDTO updatedBooking = bookingService.requestRefund(bookingID, refundDTO);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Refund Request Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi yêu cầu hoàn tiền: " + e.getMessage()
            );
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @PostMapping("/admin/search")
    public ResponseEntity<Page<BookingResponseDTO>> searchBookings(
            @RequestBody BookingSearchRequestDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);


        Page<BookingResponseDTO> responsePage = bookingService.searchBookings(searchDTO, pageable);

        return ResponseEntity.ok(responsePage);
    }
    @PostMapping("/admin/update-status")
    public ResponseEntity<?> updateBookingStatus(
            @Valid @RequestBody BookingUpdateStatusRequestDTO requestDTO
    ) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(requestDTO);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Update Status Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi cập nhật trạng thái: " + e.getMessage()
            );
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
