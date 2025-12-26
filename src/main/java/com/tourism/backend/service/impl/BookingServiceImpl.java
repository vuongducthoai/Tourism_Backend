package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingSearchRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingUpdateStatusRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.convert.BookingConverter;
import com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO;
import com.tourism.backend.dto.requestDTO.RefundInformationRequestDTO;
import com.tourism.backend.dto.response.BookingFlightDTO;
import com.tourism.backend.dto.response.CouponDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.TransactionVerificationDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.BookingService;
import com.tourism.backend.service.MailService;
import com.tourism.backend.service.SepayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final TourRepository tourRepository;
    private final TourDepartureRepository departureRepository;
    private final CouponRepository couponRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingConverter bookingConverter;
    private final RefundInformationRepository refundInformationRepository;
    private final MailService mailService;
    private static final BigDecimal COIN_RATE = new BigDecimal("1000");
    private final WebSocketService webSocketService;
    private final SepayService sepayService;
    @Override
    public TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId) {
        Tour tour = tourRepository.findByTourCode(tourCode)
                .orElseThrow(() -> new RuntimeException("Tour not found with tour code: " + tourCode));

        String tourImage = (tour.getImages() != null && !tour.getImages().isEmpty())
                ? tour.getImages().get(0).getImageURL()
                : null;

        TourBookingInfoDTO dto = new TourBookingInfoDTO();
        dto.setTourId(tour.getTourID());
        dto.setTourName(tour.getTourName());
        dto.setTourCode(tour.getTourCode());
        dto.setImage(tourImage);

        if (departureId != null) {
            TourDeparture departure = departureRepository.findById(departureId)
                    .orElseThrow(() -> new RuntimeException("Departure not found"));

            // Số chỗ còn lại
            dto.setAvailableSlots(departure.getAvailableSlots());

            // Giá tour
            List<DeparturePricing> pricings = departure.getPricings();
            dto.setAdultPrice(findPriceByType(pricings, PassengerType.ADULT));
            dto.setChildPrice(findPriceByType(pricings, PassengerType.CHILD));
            dto.setInfantPrice(findPriceByType(pricings, PassengerType.INFANT));
            dto.setSingleRoomSurcharge(findPriceByType(pricings, PassengerType.SINGLE_SUPPLEMENT));

            // Chuyến bay
            List<DepartureTransport> transports = departure.getTransports();
            if (transports != null && !transports.isEmpty()) {
                transports.sort(Comparator.comparing(DepartureTransport::getDepartTime));
                dto.setOutboundFlight(mapToFlightDTO(transports.get(0)));
                if (transports.size() > 1) {
                    dto.setInboundFlight(mapToFlightDTO(transports.get(transports.size() - 1)));
                }
            }

            LocalDateTime now = LocalDateTime.now();

            // 1. Lấy Coupon dành riêng cho Departure (Ưu tiên cao nhất)
            Coupon linkedCoupon = departure.getCoupon();

            if (linkedCoupon != null &&
                    linkedCoupon.getStartDate().isBefore(now) &&
                    linkedCoupon.getEndDate().isAfter(now) &&
                    (linkedCoupon.getUsageCount() < linkedCoupon.getUsageLimit())) {

                dto.setDepartureCoupon(mapToCouponDTO(linkedCoupon));
            }

            // 2. Lấy danh sách Coupon Global (Cho khách chọn thêm)
            List<Coupon> globalCoupons = couponRepository.findActiveGlobalCoupons(now);
            dto.setGlobalCoupons(
                    globalCoupons.stream()
                            .map(this::mapToCouponDTO)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO createBooking(BookingRequestDTO request, String authenticatedEmail) {
        TourDeparture departure = departureRepository.findById(request.getDepartureId())
                .orElseThrow(() -> new RuntimeException("Departure not found!"));

        // Count seat
        long seatCount = request.getPassengers().stream()
                .filter(p -> !"INFANT".equalsIgnoreCase(p.getType()))
                .count();

        int updatedRows = departureRepository.decreaseAvailableSlots(request.getDepartureId(), (int) seatCount);
        if (updatedRows == 0) {
            throw new RuntimeException("Regret, there are not enough seats or they are already booked");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal subSurcharge = BigDecimal.ZERO;
        List<DeparturePricing> pricings = departure.getPricings();

        Booking booking = new Booking();
        List<BookingPassenger> passengerEntities = new ArrayList<>();

        // Process passengers
        for (BookingRequestDTO.PassengerRequest pReq : request.getPassengers()) {
            BigDecimal ticketPrice = findPriceByType(pricings, pReq.getType());

            BigDecimal surcharge = BigDecimal.ZERO;
            if (pReq.isSingleRoom()) {
                surcharge = findPriceByType(pricings, "SINGLE_SUPPLEMENT");
            }

            subSurcharge = subSurcharge.add(surcharge);
            subTotal = subTotal.add(ticketPrice);

            BookingPassenger pEntity = new BookingPassenger();
            pEntity.setFullName(pReq.getFullName());
            pEntity.setGender(pReq.getGender());
            pEntity.setDateOfBirth(pReq.getDateOfBirth());
            pEntity.setPassengerType(pReq.getType());
            pEntity.setBasePrice(ticketPrice);
            pEntity.setRequiresSingleRoom(pReq.isSingleRoom());
            pEntity.setSingleRoomSurcharge(surcharge);
            pEntity.setBooking(booking);
            passengerEntities.add(pEntity);
        }

        BigDecimal totalBeforeDiscount = subTotal.add(subSurcharge);

        // ===== XỬ LÝ COUPON =====
        BigDecimal couponDiscount = BigDecimal.ZERO;
        List<Coupon> appliedCoupons = new ArrayList<>();

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            for (String code : request.getCouponCode()) {
                Coupon coupon = couponRepository.findByCouponCode(code)
                        .orElseThrow(() -> new RuntimeException("Coupon code not found: " + code));

                if (coupon.getMinOrderValue() != null && totalBeforeDiscount.compareTo(coupon.getMinOrderValue()) < 0) {
                    throw new RuntimeException(
                            "Order total must be at least " +
                                    String.format("%,.0f", coupon.getMinOrderValue()) +
                                    " VND to use coupon " + code +
                                    ". Current total: " + String.format("%,.0f", totalBeforeDiscount) + " VND"
                    );
                }

                couponDiscount = couponDiscount.add(BigDecimal.valueOf(coupon.getDiscountAmount()));
                coupon.setUsageCount(coupon.getUsageCount() + 1);
                couponRepository.save(coupon);
                appliedCoupons.add(coupon);
            }
        }

        // ===== TÌM USER (NẾU ĐÃ ĐĂNG NHẬP) =====
        User user = null;
        if (authenticatedEmail != null) {
            user = userRepository.findByEmail(authenticatedEmail).orElse(null);

            if (user != null) {
                System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getUserID() + ")");
            } else {
                System.out.println("User not found with email: " + authenticatedEmail);
            }
        }

        BigDecimal pointDiscount = BigDecimal.ZERO;

        if (user != null && request.getPointsUsed() != null && request.getPointsUsed() > 0) {
            if (!request.getContactEmail().equalsIgnoreCase(authenticatedEmail)) {
                throw new RuntimeException(
                        "⚠️ Email liên lạc (" + request.getContactEmail() + ") " +
                                "phải trùng với email tài khoản (" + authenticatedEmail + ") " +
                                "để sử dụng điểm thưởng!"
                );
            }

            BigDecimal pointsToRedeem = BigDecimal.valueOf(request.getPointsUsed());

            if (user.getCoinBalance().compareTo(pointsToRedeem) < 0) {
                throw new RuntimeException(
                        "Insufficient points balance! You just have " +
                                user.getCoinBalance() + " point."
                );
            }

            pointDiscount = pointsToRedeem.multiply(BigDecimal.valueOf(1000));
            System.out.println(pointsToRedeem + "edqwdwqdqwdqw");
            user.setCoinBalance(user.getCoinBalance().subtract(pointsToRedeem));

            System.out.println("Points used: " + request.getPointsUsed() +
                    " → Discount: " + pointDiscount);
        }

        BigDecimal finalTotal = totalBeforeDiscount.subtract(couponDiscount).subtract(pointDiscount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);

        booking.setContactFullName(request.getContactFullName());
        booking.setContactEmail(request.getContactEmail());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactAddress(request.getContactAddress());
        booking.setCustomerNote(request.getCustomerNote());

        booking.setTotalPassengers(request.getPassengers().size());
        booking.setSubtotalPrice(subTotal);
        booking.setSurcharge(subSurcharge);
        booking.setCouponDiscount(couponDiscount);
        booking.setPaidByCoin(pointDiscount);
        booking.setTotalPrice(finalTotal);

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            booking.setAppliedCouponCodes(String.join(",", request.getCouponCode()));
        }

        booking.setTourDeparture(departure);
        booking.setPassengers(passengerEntities);

        if (user != null) {
            booking.setUser(user);
            userRepository.save(user);
            System.out.println("Booking linked to user: " + user.getEmail());
        } else {
            System.out.println("Booking created as guest (no user)");
        }

        bookingRepository.save(booking);

        return mapToBookingDetailDTO(booking);
    }

    @Override
    public BookingDetailResponseDTO getBookingDetail(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found with code: " + bookingCode));
        return mapToBookingDetailDTO(booking);
    }

    private BookingDetailResponseDTO mapToBookingDetailDTO(Booking booking) {
        Tour tour = booking.getTourDeparture().getTour();
        TourDeparture departure = booking.getTourDeparture();

        List<BookingDetailResponseDTO.PassengerDTO> passengerDTOs = booking.getPassengers().stream()
                .map(p -> BookingDetailResponseDTO.PassengerDTO.builder()
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .dateOfBirth(p.getDateOfBirth().toString())
                        .type(p.getPassengerType())
                        .singleRoom(p.getRequiresSingleRoom())
                        .build())
                .collect(Collectors.toList());

        List<DepartureTransport> transports = departure.getTransports();
        BookingFlightDTO outbound = null;
        BookingFlightDTO inbound = null;

        if (transports != null && !transports.isEmpty()) {
            transports.sort(Comparator.comparing(DepartureTransport::getDepartTime));
            outbound = mapToFlightDTO(transports.get(0));

            if (transports.size() > 1) {
                inbound = mapToFlightDTO(transports.get(transports.size() - 1));
            }
        }

        List<String> appliedCouponCodes = new ArrayList<>();
        if (booking.getAppliedCouponCodes() != null && !booking.getAppliedCouponCodes().isEmpty()) {
            appliedCouponCodes = Arrays.asList(booking.getAppliedCouponCodes().split(","));
        }

        BigDecimal paid = BigDecimal.ZERO;

        return BookingDetailResponseDTO.builder()
                .bookingCode(booking.getBookingCode())
                .createdDate(booking.getCreatedAt())
                .status(booking.getBookingStatus().name())
                .paymentDeadline(booking.getCreatedAt().plusHours(24))

                // Financial
                .originalPrice(booking.getTotalPrice())
                .paidAmount(paid)
                .remainingAmount(booking.getTotalPrice().subtract(paid))

                // Lists
                .passengers(passengerDTOs)
                .appliedCouponCodes(appliedCouponCodes)
                .outboundTransport(outbound)
                .inboundTransport(inbound)

                // Tour Info
                .tourName(tour.getTourName())
                .tourCode(tour.getTourCode())
                .tourImage(tour.getImages().isEmpty() ? null : tour.getImages().get(0).getImageURL())
                .duration(tour.getDuration())
                .build();
    }

    private BigDecimal findPriceByType(List<DeparturePricing> pricings, String typeName) {
        return pricings.stream()
                .filter(p -> p.getPassengerType().name().equals(typeName))
                .findFirst()
                .map(DeparturePricing::getSalePrice)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findPriceByType(List<DeparturePricing> pricings, PassengerType type) {
        if (pricings == null) return BigDecimal.ZERO;
        return pricings.stream()
                .filter(p -> p.getPassengerType() == type)
                .findFirst()
                .map(DeparturePricing::getSalePrice)
                .orElse(BigDecimal.ZERO);
    }

    private CouponDTO mapToCouponDTO(Coupon coupon) {
        return new CouponDTO(
                coupon.getCouponID(),
                coupon.getCouponCode(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getMinOrderValue()
        );
    }

    private BookingFlightDTO mapToFlightDTO(DepartureTransport transport) {
        BookingFlightDTO flight = new BookingFlightDTO();
        flight.setTransportCode(transport.getTransportCode());
        flight.setAirlineName(transport.getVehicleName());
        flight.setStartPoint(transport.getStartPoint());
        flight.setEndPoint(transport.getEndPoint());
        flight.setVehicleType(transport.getVehicleTyle().name());
        flight.setVehicleName(transport.getVehicleName());
        flight.setStartPointName(getLocationName(transport.getStartPoint()));
        flight.setEndPointName(getLocationName(transport.getEndPoint()));
        flight.setDepartTime(transport.getDepartTime());
        flight.setArrivalTime(transport.getArrivalTime());
        return flight;
    }

    private String getLocationName(String locationCode) {
        if (locationCode == null) return "";
        return locationRepository.findByAirportCode(locationCode)
                .map(Location::getAirportName)
                .orElse(locationCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookingsByUser(Integer userID, BookingStatus bookingStatus) {
        List<Booking> bookings = bookingRepository.findByUserIDWithDetails(userID, bookingStatus);
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .map(bookingConverter::convertToBookingResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public BookingResponseDTO cancelBooking(BookingCancellationRequestDTO requestDTO) {
        // 1. Tìm Booking
        Booking booking = bookingRepository.findById(requestDTO.getBookingID())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + requestDTO.getBookingID()));

        // 2. Kiểm tra trạng thái hủy (Chỉ hủy khi đơn chưa bị hủy, đã thanh toán, v.v.)
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        BigDecimal coinRefundAmount = BigDecimal.ZERO;

        // Số tiền đã được giảm giá bằng Coin
        BigDecimal paidByCoin = booking.getPaidByCoin() != null ? booking.getPaidByCoin() : BigDecimal.ZERO;
        if (paidByCoin.compareTo(BigDecimal.ZERO) > 0) {
            // Chỉ hoàn lại số Coin đã dùng để thanh toán
            coinRefundAmount = paidByCoin.divide(COIN_RATE, 0, java.math.RoundingMode.DOWN);
        }
        BigDecimal totalValue = booking.getTotalPrice().add(paidByCoin);
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            // Số Coin hoàn lại
            coinRefundAmount = totalValue.divide(COIN_RATE, 0, java.math.RoundingMode.DOWN);
            // Tiền mặt hoàn lại (vì đã quy hết thành coin)refundAmount = BigDecimal.ZERO;
        } else {
            coinRefundAmount = BigDecimal.ZERO;
        }


        // 4. Cập nhật số dư Coin cho User
        User user = booking.getUser();
        if (user == null) {
            throw new RuntimeException("User associated with booking not found.");
        }

        BigDecimal currentCoinBalance = user.getCoinBalance() != null ? user.getCoinBalance() : BigDecimal.ZERO;
        BigDecimal newCoinBalance = currentCoinBalance.add(coinRefundAmount);

        user.setCoinBalance(newCoinBalance);
        userRepository.save(user); // Lưu cập nhật Coin

        // 5. Cập nhật Booking
        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        BookingResponseDTO responseDTO = bookingConverter.convertToBookingResponseDTO(updatedBooking);
        webSocketService.notifyAdminBookingUpdate(responseDTO);
//        if (booking.getUser() != null) {
//            webSocketService.notifyUserBookingUpdate(booking.getUser().getUserID(), responseDTO);
//        }
        return responseDTO;
    }

    @Override
    public BookingResponseDTO requestRefund(Integer bookingID, RefundInformationRequestDTO refundDTO) {
        // 1. Tìm Booking
        Booking booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingID));

        // 2. Kiểm tra trạng thái hợp lệ để yêu cầu hoàn tiền (Ví dụ: PAID)
        if (booking.getBookingStatus() == BookingStatus.CANCELLED || booking.getBookingStatus() == BookingStatus.PENDING_REFUND) {
            throw new RuntimeException("Booking is already in or past cancellation/refund process.");
        }

        // 3. Tính toán tổng tiền hoàn (totalPrice + paidByCoin)
        BigDecimal totalPrice = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
        BigDecimal paidByCoin = booking.getPaidByCoin() != null ? booking.getPaidByCoin() : BigDecimal.ZERO;

        // Số tiền yêu cầu hoàn = Tiền mặt phải trả + Giá trị tiền Coin đã dùng
        BigDecimal totalRefundAmount = totalPrice.add(paidByCoin);


        // 4. Tạo và Lưu RefundInformation
        RefundInformation refundInformation = new RefundInformation();
        refundInformation.setAccountName(refundDTO.getAccountName());
        refundInformation.setAccountNumber(refundDTO.getAccountNumber());
        refundInformation.setBank(refundDTO.getBank());
        refundInformation.setBooking(booking); // Liên kết với Booking

        // Kiểm tra xem đã có RefundInformation chưa (Nếu có, sẽ cập nhật thay vì tạo mới)
        if (booking.getRefundInformation() != null) {
            // Cập nhật thông tin cũ
            Integer existingRefundId = booking.getRefundInformation().getRefundID();
            refundInformation.setRefundID(existingRefundId);
        }

        RefundInformation savedRefundInfo = refundInformationRepository.save(refundInformation);
        booking.setRefundInformation(savedRefundInfo); // Cập nhật lại mối quan hệ


        // 5. Cập nhật Booking Status
        booking.setBookingStatus(BookingStatus.PENDING_REFUND);
        booking.setRefundAmount(totalRefundAmount); // Lưu lại tổng tiền yêu cầu hoàn
        Booking updatedBooking = bookingRepository.save(booking);

        // 6. Gửi Email thông báo đến Admin
        // Đảm bảo TourDeparture và Tour được Fetch để không bị lỗi LazyInitializationException khi gửi mail
        if (updatedBooking.getTourDeparture() != null && updatedBooking.getTourDeparture().getTour() != null) {
            mailService.sendRefundRequestNotification(updatedBooking, savedRefundInfo, totalRefundAmount);
        }

        // 7. Trả về Response
        BookingResponseDTO responseDTO = bookingConverter.convertToBookingResponseDTO(updatedBooking);
        webSocketService.notifyAdminBookingUpdate(responseDTO);
//        if (booking.getUser() != null) {
//            webSocketService.notifyUserBookingUpdate(booking.getUser().getUserID(), responseDTO);
//        }
        return responseDTO;

    }

    @Override
    public Page<BookingResponseDTO> searchBookings(BookingSearchRequestDTO searchDTO, Pageable pageable) {
        Page<Booking> bookingPage = bookingRepository.searchBookings(searchDTO, pageable);
        return bookingPage.map(bookingConverter::convertToBookingResponseDTO);
    }

//    @Override
//    @Transactional
//    public BookingResponseDTO updateBookingStatus(BookingUpdateStatusRequestDTO requestDTO) {
//        Booking booking = bookingRepository.findById(requestDTO.getBookingID())
//                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + requestDTO.getBookingID()));
//
//        String newStatus = requestDTO.getBookingStatus();
//        String currentStatus = booking.getBookingStatus().name();
//
//        switch (newStatus) {
//            case "PAID":
//                if (!currentStatus.equals("PENDING_CONFIRMATION")) {
//                    throw new RuntimeException("Chỉ có thể xác nhận booking ở trạng thái 'Chờ xác nhận'");
//                }
//                booking.setBookingStatus(BookingStatus.PAID);
//                mailService.sendPaymentConfirmationEmail(booking);
//                break;
//
//            case "CANCELLED":
//                if (!List.of("PENDING_PAYMENT", "PENDING_CONFIRMATION", "PAID", "PENDING_REFUND")
//                        .contains(currentStatus)) {
//                    throw new RuntimeException("Không thể hủy booking ở trạng thái hiện tại");
//                }
//
//                booking.setBookingStatus(BookingStatus.CANCELLED);
//                booking.setCancelReason(requestDTO.getCancelReason());
//
//                // XỬ LÝ HOÀN TIỀN QUA SEPAY
//                if (currentStatus.equals("PENDING_CONFIRMATION") ||
//                        currentStatus.equals("PAID") ||
//                        currentStatus.equals("PENDING_REFUND")) {
//
//                    BigDecimal refundAmount = booking.getTotalPrice().add(
//                            booking.getPaidByCoin() != null ? booking.getPaidByCoin() : BigDecimal.ZERO
//                    );
//                    booking.setRefundAmount(refundAmount);
//
//                    // Xác định thông tin tài khoản hoàn tiền
//                    String accountNumber = null;
//                    String accountName = null;
//                    String bankCode = null;
//
//                    // Ưu tiên RefundInformation, nếu không có thì dùng Payment
//                    if (booking.getRefundInformation() != null) {
//                        RefundInformation refundInfo = booking.getRefundInformation();
//                        accountNumber = refundInfo.getAccountNumber();
//                        accountName = refundInfo.getAccountName();
//                        bankCode = refundInfo.getBank();
//                        log.info("Using RefundInformation for refund: {}", accountNumber);
//                    } else if (booking.getPayment() != null) {
//                        Payment payment = booking.getPayment();
//                        accountNumber = payment.getAccountNumber();
//                        accountName = payment.getAccountName();
//                        bankCode = payment.getBank();
//                        log.info("Using Payment information for refund: {}", accountNumber);
//                    }
//
//                    // Thực hiện hoàn tiền qua SePay
//                    if (accountNumber != null && accountName != null && bankCode != null) {
//                        String description = String.format(
//                                "Hoan tien booking %s - Tour %s",
//                                booking.getBookingCode(),
//                                booking.getTourDeparture().getTour().getTourCode()
//                        );
//
//                        boolean transferSuccess = sepayService.transferRefund(
//                                accountNumber,
//                                accountName,
//                                bankCode,
//                                refundAmount,
//                                description
//                        );
//
//                        if (transferSuccess) {
//                            log.info("SePay refund successful for booking: {}", booking.getBookingCode());
//                            mailService.sendCancellationWithRefundEmail(booking, refundAmount);
//                        } else {
//                            log.error("SePay refund failed for booking: {}", booking.getBookingCode());
//                            mailService.sendCancellationWithRefundEmail(booking, refundAmount);
//                        }
//                    } else {
//                        log.warn("No account information for refund. Booking: {}", booking.getBookingCode());
//                        mailService.sendCancellationWithRefundEmail(booking, refundAmount);
//                    }
//                } else {
//                    mailService.sendCancellationEmail(booking);
//                }
//                break;
//
//            default:
//                throw new RuntimeException("Trạng thái không hợp lệ: " + newStatus);
//        }
//
//        Booking updatedBooking = bookingRepository.save(booking);
//        BookingResponseDTO responseDTO = bookingConverter.convertToBookingResponseDTO(updatedBooking);
//
//        webSocketService.notifyAdminBookingUpdate(responseDTO);
//
//        if (booking.getUser() != null) {
//            webSocketService.notifyUserBookingUpdate(booking.getUser().getUserID(), responseDTO);
//        }
//
//        return responseDTO;
//    }
@Override
@Transactional
public BookingResponseDTO updateBookingStatus(BookingUpdateStatusRequestDTO requestDTO) {
    Booking booking = bookingRepository.findById(requestDTO.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + requestDTO.getBookingID()));

    String newStatus = requestDTO.getBookingStatus();
    String currentStatus = booking.getBookingStatus().name();

    switch (newStatus) {
        case "PAID":
            if (!currentStatus.equals("PENDING_CONFIRMATION")) {
                throw new RuntimeException("Chỉ có thể xác nhận booking ở trạng thái 'Chờ xác nhận'");
            }
            booking.setBookingStatus(BookingStatus.PAID);
            mailService.sendPaymentConfirmationEmail(booking);
            break;

        case "CANCELLED":
            if (!List.of("PENDING_PAYMENT", "PENDING_CONFIRMATION", "PAID", "PENDING_REFUND")
                    .contains(currentStatus)) {
                throw new RuntimeException("Không thể hủy booking ở trạng thái hiện tại");
            }

            // ✅ KIỂM TRA GIAO DỊCH HOÀN TIỀN QUA SEPAY
            if (currentStatus.equals("PENDING_CONFIRMATION") ||
                    currentStatus.equals("PAID") ||
                    currentStatus.equals("PENDING_REFUND")) {

                BigDecimal refundAmount = booking.getTotalPrice().add(
                        booking.getPaidByCoin() != null ? booking.getPaidByCoin() : BigDecimal.ZERO
                );

                // Lấy thông tin tài khoản hoàn tiền
                String accountNumber = null;
                String accountName = null;
                String bank = null;

                if (booking.getRefundInformation() != null) {
                    accountNumber = booking.getRefundInformation().getAccountNumber();
                    accountName = booking.getRefundInformation().getAccountName();
                    bank = booking.getRefundInformation().getBank();
                } else if (booking.getPayment() != null) {
                    accountNumber = booking.getPayment().getAccountNumber();
                    accountName = booking.getPayment().getAccountName();
                    bank = booking.getPayment().getBank();
                }

                // ✅ VERIFY GIAO DỊCH QUA SEPAY
                if (accountNumber != null) {
                    log.info("🔍 Checking SePay transactions for booking: {}", booking.getBookingCode());

                    TransactionVerificationDTO verification = sepayService.verifyRefundTransaction(
                            booking.getBookingCode(),
                            refundAmount,
                            accountNumber,
                            accountName,
                            bank
                    );

                    if (!verification.isVerified()) {
                        throw new RuntimeException(
                                "⚠️ Không tìm thấy giao dịch hoàn tiền khớp trong lịch sử 24h gần đây. " +
                                        "Vui lòng kiểm tra lại hoặc chuyển khoản theo đúng thông tin."
                        );
                    }

                    log.info("✅ Transaction verified: {}", verification.getTransactionReference());
                }

                booking.setRefundAmount(refundAmount);
                mailService.sendCancellationWithRefundEmail(booking, refundAmount);

            } else {
                mailService.sendCancellationEmail(booking);
            }

            booking.setBookingStatus(BookingStatus.CANCELLED);
            booking.setCancelReason(requestDTO.getCancelReason());
            break;

        default:
            throw new RuntimeException("Trạng thái không hợp lệ: " + newStatus);
    }

    Booking updatedBooking = bookingRepository.save(booking);
    BookingResponseDTO responseDTO = bookingConverter.convertToBookingResponseDTO(updatedBooking);

    // WebSocket notification
    webSocketService.notifyAdminBookingUpdate(responseDTO);
    if (booking.getUser() != null) {
        webSocketService.notifyUserBookingUpdate(booking.getUser().getUserID(), responseDTO);
    }

    return responseDTO;
}
}