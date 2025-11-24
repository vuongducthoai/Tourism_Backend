-- 1. Trụ sở chính tại Hà Nội
INSERT INTO branch_contacts (
    branch_name, phone, email, address, is_head_office,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Trụ sở chính Hà Nội',
    '02439999999',
    'support.hn@tourism.com',
    '123 Phố Huế, Quận Hoàn Kiếm, Hà Nội',
    true, -- Là trụ sở chính
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. Chi nhánh TP. Hồ Chí Minh
INSERT INTO branch_contacts (
    branch_name, phone, email, address, is_head_office,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Văn phòng đại diện TP.HCM',
    '02838888888',
    'support.hcm@tourism.com',
    '45 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh',
    false, -- Chi nhánh phụ
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 3. Chi nhánh Đà Nẵng
INSERT INTO branch_contacts (
    branch_name, phone, email, address, is_head_office,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Văn phòng Đà Nẵng',
    '02363777777',
    'support.dn@tourism.com',
    '20 Bạch Đằng, Quận Hải Châu, Đà Nẵng',
    false,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 4. Chi nhánh Cần Thơ (Miền Tây)
INSERT INTO branch_contacts (
    branch_name, phone, email, address, is_head_office,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Văn phòng Cần Thơ',
    '02923666666',
    'support.cantho@tourism.com',
    '10 Ninh Kiều, Quận Ninh Kiều, Cần Thơ',
    false,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);