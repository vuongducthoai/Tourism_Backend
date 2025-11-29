-- =================================================================================
-- PHẦN 2: TẠO GIÁ TIỀN (DEPARTURE_PRICING)
-- Logic: Phải map đúng vào cái Departure vừa tạo ở trên (Dùng ngày + mã tour để tìm)
-- =================================================================================
-- 1. GIÁ CHO TOUR HẠ LONG (Khởi hành ngày 02/09/2025)
-- Tour Code: TOUR-HL-01
INSERT INTO departure_pricing (
    passenger_type, age_description, sale_price, original_price, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
-- Người lớn (Giảm giá dịp lễ)
(
    'ADULT', 'Từ 11 tuổi trở lên',
    3500000, 4200000, -- Bán 3.5tr, Gốc 4.2tr
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Trẻ em
(
    'CHILD', 'Từ 5-10 tuổi',
    2450000, 2900000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Phụ thu phòng đơn (Giá gốc = Giá bán, thường không giảm)
(
    'SINGLE_SUPPLEMENT', 'Phụ thu phòng đơn',
    1500000, 1500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);


-- 2. GIÁ CHO TOUR PHÚ QUỐC (Khởi hành ngày 20/11/2025)
-- Tour Code: TOUR-PQ-03
INSERT INTO departure_pricing (
    passenger_type, age_description, sale_price, original_price, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
-- Người lớn (Đang có khuyến mãi kích cầu)
(
    'ADULT', 'Từ 12 tuổi trở lên',
    6990000, 8500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Trẻ em
(
    'CHILD', 'Từ 2-11 tuổi',
    6290000, 7500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Em bé (Phí sân bay cố định, không có giá gốc cao hơn)
(
    'INFANT', 'Dưới 2 tuổi',
    500000, 500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Phụ thu phòng đơn
(
    'SINGLE_SUPPLEMENT', 'Phụ thu phòng đơn',
    2500000, 2500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);


-- 3. GIÁ CHO TOUR HÀ GIANG (Khởi hành ngày 15/10/2025)
-- Tour Code: TOUR-HG-04
INSERT INTO departure_pricing (
    passenger_type, age_description, sale_price, original_price, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'ADULT', 'Người lớn',
    2800000, 3200000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'CHILD', 'Trẻ em 5-10 tuổi',
    2100000, 2500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'SINGLE_SUPPLEMENT', 'Ngủ riêng 1 mình',
    800000, 800000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);


-- 4. GIÁ CHO TOUR HUẾ (Khởi hành ngày 15/08/2025)
-- Tour Code: TOUR-DN-HUE-01
INSERT INTO departure_pricing (
    passenger_type, age_description, sale_price, original_price, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'ADULT', 'Vé tham quan + Ăn trưa',
    850000, 1000000, -- Đang giảm giá mạnh
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-08-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);