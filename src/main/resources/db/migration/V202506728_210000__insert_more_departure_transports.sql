-- =================================================================================
-- CHÈN DỮ LIỆU VẬN CHUYỂN RIÊNG CHO TOUR HẠ LONG (TOUR-HL-01)
-- Tour 2 Ngày 1 Đêm -> Ngày về sẽ là ngày hôm sau
-- =================================================================================

-- ---------------------------------------------------------------------------------
-- 1. LỊCH KHỞI HÀNH: 02/09/2025 (Dịp lễ)
-- ---------------------------------------------------------------------------------

-- 1.1. Chiều đi (SGN -> HAN)
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'OUTBOUND', 'VN206', 'Vietnam Airlines',
    'SGN', 'HAN', -- Mã sân bay
    '2025-09-02 06:00:00', '2025-09-02 08:05:00',
    -- Sub-query lấy ID của ngày 02/09
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.2. Chiều về (HAN -> SGN) - Về ngày 03/09
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'INBOUND', 'VN265', 'Vietnam Airlines',
    'HAN', 'SGN',
    '2025-09-03 18:00:00', '2025-09-03 20:10:00',
    -- Vẫn trỏ vào departure_id của ngày đi (02/09)
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);


-- ---------------------------------------------------------------------------------
-- 2. LỊCH KHỞI HÀNH: 15/10/2025 (Ngày thường)
-- ---------------------------------------------------------------------------------

-- 2.1. Chiều đi (SGN -> HAN)
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'OUTBOUND', 'VJ450', 'Vietjet Air',
    'SGN', 'HAN',
    '2025-10-15 09:15:00', '2025-10-15 11:20:00',
    -- Sub-query lấy ID của ngày 15/10
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2.2. Chiều về (HAN -> SGN) - Về ngày 16/10
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'INBOUND', 'VJ451', 'Vietjet Air',
    'HAN', 'SGN',
    '2025-10-16 14:00:00', '2025-10-16 16:05:00',
    -- Vẫn trỏ vào departure_id của ngày đi (15/10)
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-10-15' AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);