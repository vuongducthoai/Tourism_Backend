-- =================================================================================
-- TẠO DỮ LIỆU TOUR YÊU THÍCH (WISHLIST)
-- Logic: Lấy ID của User (qua email) và ID của Tour (qua tour_code)
-- =================================================================================

-- 1. Khách VIP (khachvip@gmail.com) thích 2 tour sang chảnh (Hạ Long, Phú Quốc)
INSERT INTO favorite_tours (
    user_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    (SELECT userid FROM users WHERE email = 'khachvip@gmail.com' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    (SELECT userid FROM users WHERE email = 'khachvip@gmail.com' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. Khách Thường (khachthuong@gmail.com) thích tour Huế và Hà Giang
INSERT INTO favorite_tours (
    user_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    (SELECT userid FROM users WHERE email = 'khachthuong@gmail.com' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    (SELECT userid FROM users WHERE email = 'khachthuong@gmail.com' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. Admin cũng test thử thích 1 tour
INSERT INTO favorite_tours (
    user_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES
(
    (SELECT userid FROM users WHERE email = 'admin@tourism.com' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);