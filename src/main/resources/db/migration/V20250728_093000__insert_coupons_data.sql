-- =================================================================================
-- TẠO DỮ LIỆU MÃ GIẢM GIÁ (COUPONS)
-- =================================================================================

-- 1. Coupon CHÀO MỪNG (Đang hoạt động, không giới hạn đơn tối thiểu)
-- Giảm 200k, Hạn đến hết năm 2026
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'WELCOME2025', 200000,
    '2024-01-01 00:00:00', '2026-12-31 23:59:59',
    1000, 50, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. Coupon GIẢM SÂU (Đang hoạt động, Kèm điều kiện đơn tối thiểu cao)
-- Giảm 500k cho đơn từ 5 triệu
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'BIGSALE500', 500000,
    '2024-01-01 00:00:00', '2026-12-31 23:59:59',
    100, 10, 5000000,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. Coupon ĐÃ HẾT HẠN (Test case: validate ngày hết hạn)
-- Đã hết hạn từ năm 2023
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'EXPIRED2023', 100000,
    '2023-01-01 00:00:00', '2023-12-31 23:59:59',
    1000, 10, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 4. Coupon CHƯA DIỄN RA (Test case: validate ngày bắt đầu)
-- Áp dụng cho tương lai (Năm 2030)
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'FUTURE2030', 999000,
    '2030-01-01 00:00:00', '2030-12-31 23:59:59',
    100, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 5. Coupon ĐÃ HẾT LƯỢT DÙNG (Test case: validate usage limit)
-- Giới hạn 50, đã dùng 50
INSERT INTO coupons (
    coupon_code, discount_amount,
    start_date, end_date,
    usage_limit, usage_count, min_order_value,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'SOLDOUT', 50000,
    '2024-01-01 00:00:00', '2026-12-31 23:59:59',
    50, 50, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);