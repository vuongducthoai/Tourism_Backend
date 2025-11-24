-- =================================================================================
-- KỊCH BẢN 1: ĐẶT TOUR THÀNH CÔNG (ĐÃ THANH TOÁN)
-- =================================================================================

-- 1.1. Tạo Booking
INSERT INTO bookings (
    booking_code, booking_date,
    contact_full_name, contact_email, contact_phone, contact_address, customer_note,
    total_passengers,
    subtotal_price, surcharge, discount_amount, paid_by_coin, total_price,
    booking_status, -- Status của Booking thì vẫn dùng PAID ok
    user_id, departure_id, coupon_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'BK-VIP-001', '2025-08-01 10:00:00',
    'Nguyễn Văn Giàu', 'khachvip@gmail.com', '0909000002', 'Biệt thự A1, Quận 7, TP.HCM', 'Sắp xếp phòng view biển đẹp nhất',
    2,
    7000000,
    0, 0, 0, 7000000,
    'PAID', -- Enum BookingStatus
    (SELECT userid FROM users WHERE email = 'khachvip@gmail.com'),
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    NULL,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.2. Tạo Hành khách
INSERT INTO booking_passengers (
    full_name, gender, date_of_birth, passenger_type,
    base_price, requires_single_room, single_room_surcharge,
    booking_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'Nguyễn Văn Giàu', 'Nam', '1995-05-15', 'ADULT',
    3500000, false, 0,
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-VIP-001'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Trần Thị Mây', 'Nữ', '1996-08-20', 'ADULT',
    3500000, false, 0,
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-VIP-001'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.3. Tạo Payment (SỬA LẠI STATUS TẠI ĐÂY)
INSERT INTO payments (
    amount, status, booking_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    7000000,
    'SUCCESS', -- Sửa từ 'PAID' thành 'SUCCESS' (cho khớp với Enum PaymentStatus)
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-VIP-001'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);


-- =================================================================================
-- KỊCH BẢN 2: ĐẶT TOUR CHƯA THANH TOÁN
-- =================================================================================

-- 2.1. Tạo Booking
INSERT INTO bookings (
    booking_code, booking_date,
    contact_full_name, contact_email, contact_phone, contact_address, customer_note,
    total_passengers,
    subtotal_price, surcharge, discount_amount, paid_by_coin, total_price,
    booking_status,
    user_id, departure_id, coupon_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'BK-NORMAL-002', '2025-10-01 14:30:00',
    'Trần Thị Thường', 'khachthuong@gmail.com', '0909000003', 'Chung cư B, Hà Nội', NULL,
    2,
    13280000,
    0, 200000, 0, 13080000,
    'PENDING_PAYMENT', -- Enum BookingStatus
    (SELECT userid FROM users WHERE email = 'khachthuong@gmail.com'),
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    (SELECT couponid FROM coupons WHERE coupon_code = 'WELCOME2025'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2.2. Tạo Hành khách
INSERT INTO booking_passengers (
    full_name, gender, date_of_birth, passenger_type,
    base_price, requires_single_room, single_room_surcharge,
    booking_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'Trần Thị Thường', 'Nữ', '1998-10-20', 'ADULT',
    6990000, false, 0,
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-NORMAL-002'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Lê Văn Tí', 'Nam', '2018-01-01', 'CHILD',
    6290000, false, 0,
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-NORMAL-002'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2.3. Tạo Payment (SỬA LẠI STATUS TẠI ĐÂY)
INSERT INTO payments (
    amount, status, booking_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    13080000,
    'PENDING', -- Sửa từ 'PENDING_PAYMENT' thành 'PENDING'
    (SELECT bookingid FROM bookings WHERE booking_code = 'BK-NORMAL-002'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);