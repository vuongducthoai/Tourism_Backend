-- =================================================================================
-- TẠO DỮ LIỆU MÃ GIẢM GIÁ (COUPONS)
-- Logic:
-- 1. Global Coupon: departure_id = NULL
-- 2. Departure Coupon: departure_id = (SELECT ...)
-- =================================================================================

-- 1. COUPON TOÀN SÀN (GLOBAL) - Chào mừng thành viên mới
-- Giảm 200k, không giới hạn đơn tối thiểu, áp dụng cho mọi tour
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    departure_id, -- Cột này để NULL
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'WELCOME2025', 200000,
    '2024-01-01 00:00:00', '2026-12-31 23:59:59',
    1000, 50, 0,
    NULL,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. COUPON GIẢM SÂU CHO ĐƠN LỚN (GLOBAL)
-- Giảm 500k cho đơn từ 5 triệu
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    departure_id, -- Cột này để NULL
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'BIGSALE500', 500000,
    '2024-01-01 00:00:00', '2026-12-31 23:59:59',
    100, 10, 5000000,
    NULL,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. COUPON XẢ VÉ GIỜ CHÓT (SPECIFIC DEPARTURE)
-- Chỉ áp dụng cho chuyến đi Hạ Long ngày 02/09/2025
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    departure_id, -- Phải điền ID của chuyến đi cụ thể
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'HL0209LASTMINUTE', 300000,
    '2025-08-01 00:00:00', '2025-09-02 23:59:59', -- Hạn đến ngày khởi hành
    10, 0, 0,
    (
        SELECT departureid FROM tour_departures
        WHERE departure_date = '2025-09-02'
        AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01')
    ),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 4. COUPON KÍCH CẦU MÙA THẤP ĐIỂM (SPECIFIC DEPARTURE)
-- Chỉ áp dụng cho chuyến đi Huế ngày 15/08/2025
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'HUE1508VIP', 100000,
    '2025-06-01 00:00:00', '2025-08-15 23:59:59',
    50, 5, 0,
    (
        SELECT departureid FROM tour_departures
        WHERE departure_date = '2025-08-15'
        AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01')
    ),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 5. COUPON ĐÃ HẾT HẠN (Để test logic valid)
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'EXPIRED2023', 100000,
    '2023-01-01 00:00:00', '2023-12-31 23:59:59',
    1000, 10, 0,
    NULL,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);