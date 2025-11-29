-- =================================================================================
-- PHẦN 1: TẠO LỊCH KHỞI HÀNH (TOUR_DEPARTURES)
-- Logic: Kết nối Tour (qua tour_code) + Policy (qua template_name)
-- =================================================================================
-- 1.1. Hạ Long: Khởi hành dịp lễ 2/9 (Dùng chính sách Chuẩn)
INSERT INTO tour_departures (
    departure_date, available_slots, tour_guide_info,
    policy_template_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    '2025-09-02', -- Ngày Quốc Khánh
    20,
    'HDV: Nguyễn Văn A (Tiếng Việt/Anh) - SDT: 0901234567. Kinh nghiệm 5 năm tuyến Hạ Long.',
    (SELECT policy_templateid FROM policy_templates WHERE template_name = 'Chính sách Hoàn Hủy Linh Hoạt (Nội Địa)' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.2. Phú Quốc: Khởi hành mùa đẹp tháng 11 (Dùng chính sách Bay & Biển)
INSERT INTO tour_departures (
    departure_date, available_slots, tour_guide_info,
    policy_template_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    '2025-11-20', -- Ngày Nhà giáo VN
    15,
    'HDV: Local Guide tại Phú Quốc - Đón tại sân bay Dương Đông.',
    (SELECT policy_templateid FROM policy_templates WHERE template_name = 'Chính sách Tour Bay & Biển Đảo' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.3. Hà Giang: Mùa hoa Tam Giác Mạch (Dùng chính sách Mạo hiểm)
INSERT INTO tour_departures (
    departure_date, available_slots, tour_guide_info,
    policy_template_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    '2025-10-15', -- Giữa mùa hoa
    25,
    'HDV: A Pao (Người bản địa) - Chuyên các cung đường đèo dốc, am hiểu văn hóa H''Mông.',
    (SELECT policy_templateid FROM policy_templates WHERE template_name = 'Chính sách Tour Khám Phá & Mạo Hiểm' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 1.4. Huế: Tour khuyến mãi hè (Dùng chính sách Khuyến mãi)
INSERT INTO tour_departures (
    departure_date, available_slots, tour_guide_info,
    policy_template_id, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    '2025-08-15',
    30,
    'HDV: Tôn Nữ Thị B - Giọng Huế chuẩn, am hiểu lịch sử Cung Đình.',
    (SELECT policy_templateid FROM policy_templates WHERE template_name = 'Chính sách Vé Khuyến Mãi (Không Hoàn Hủy)' LIMIT 1),
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

