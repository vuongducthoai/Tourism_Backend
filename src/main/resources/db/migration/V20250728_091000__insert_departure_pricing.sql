-- =================================================================================
-- TẠO GIÁ VÉ CHI TIẾT (DEPARTURE_PRICING)
-- Logic: Giá vé gắn liền với một Lịch Khởi Hành (Departure) cụ thể
-- =================================================================================

-- 1. GIÁ CHO TOUR HẠ LONG (Khởi hành ngày 02/09/2025)
-- Tour Code: TOUR-HL-01
INSERT INTO departure_pricing (
    passenger_type, age_description, money, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
-- Người lớn
(
    'ADULT',
    'Từ 11 tuổi trở lên',
    3500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Trẻ em (70% giá)
(
    'CHILD',
    'Từ 5-10 tuổi (Ngủ chung giường)',
    2450000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. GIÁ CHO TOUR PHÚ QUỐC (Khởi hành ngày 20/11/2025)
-- Tour Code: TOUR-PQ-03
INSERT INTO departure_pricing (
    passenger_type, age_description, money, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
-- Người lớn
(
    'ADULT',
    'Từ 12 tuổi trở lên',
    6990000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Trẻ em (90% giá vé máy bay + tour)
(
    'CHILD',
    'Từ 2-11 tuổi',
    6290000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Em bé (Phí sân bay)
(
    'INFANT',
    'Dưới 2 tuổi',
    500000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. GIÁ CHO TOUR HÀ GIANG (Khởi hành ngày 15/10/2025)
-- Tour Code: TOUR-HG-04
INSERT INTO departure_pricing (
    passenger_type, age_description, money, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'ADULT',
    'Người lớn (Trên 10 tuổi)',
    2800000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'CHILD',
    'Trẻ em (5-10 tuổi)',
    2100000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 4. GIÁ CHO TOUR HUẾ (Khởi hành ngày 15/08/2025)
-- Tour Code: TOUR-DN-HUE-01
INSERT INTO departure_pricing (
    passenger_type, age_description, money, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    'ADULT',
    'Đồng giá vé tham quan + Ăn trưa',
    850000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-08-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);