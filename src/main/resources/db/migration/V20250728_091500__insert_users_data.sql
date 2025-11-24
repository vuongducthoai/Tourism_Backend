-- =================================================================================
-- TẠO DỮ LIỆU USER (ADMIN, CUSTOMER, TOUR_OWNER)
-- Mật khẩu mặc định cho tất cả user: 123456
-- (Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4GXQpKog9dq1bef/JHG3i)
-- =================================================================================

-- 1. ADMIN HỆ THỐNG
INSERT INTO users (
    full_name, phone, email, role, password,
    status, date_of_birth, coin_balance,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Quản Trị Viên Hệ Thống',
    '0909000001',
    'admin@tourism.com',
    'ADMIN',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4GXQpKog9dq1bef/JHG3i', -- Pass: 123456
    true,
    '1990-01-01',
    0, -- Admin không cần coin
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. KHÁCH HÀNG VIP (Có nhiều tiền trong ví Coin)
INSERT INTO users (
    full_name, phone, email, role, password,
    status, date_of_birth, coin_balance,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Nguyễn Văn Giàu (VIP)',
    '0909000002',
    'khachvip@gmail.com',
    'CUSTOMER',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4GXQpKog9dq1bef/JHG3i', -- Pass: 123456
    true,
    '1995-05-15',
    10000000, -- Có 10 triệu xu để test thanh toán
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. KHÁCH HÀNG THƯỜNG (Ví 0 đồng)
INSERT INTO users (
    full_name, phone, email, role, password,
    status, date_of_birth, coin_balance,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Trần Thị Thường',
    '0909000003',
    'khachthuong@gmail.com',
    'CUSTOMER',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4GXQpKog9dq1bef/JHG3i', -- Pass: 123456
    true,
    '1998-10-20',
    0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 4. CHỦ TOUR (TOUR OWNER - Nếu sau này mở rộng chức năng đối tác)
INSERT INTO users (
    full_name, phone, email, role, password,
    status, date_of_birth, coin_balance,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Công Ty Du Lịch Việt',
    '0909000004',
    'partner@tourism.com',
    'TOUR_OWNER',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4GXQpKog9dq1bef/JHG3i', -- Pass: 123456
    true,
    '1985-12-12',
    0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);