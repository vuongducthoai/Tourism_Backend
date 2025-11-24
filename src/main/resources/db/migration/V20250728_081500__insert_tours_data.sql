-- Tour 1: Hà Nội -> Hạ Long (Quảng Ninh)
INSERT INTO tours (
    tour_code, tour_name, duration, transportation,
    start_location_id, end_location_id,
    attractions, meals, ideal_time,
    trip_transportation, suitable_customer, hotel,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'TOUR-HL-01',
    'Du thuyền Hạ Long 2N1D - Ngủ đêm trên vịnh',
    '2 Ngày 1 Đêm',
    'Xe Limousine đưa đón',
    (SELECT locationid FROM locations WHERE slug = 'ha-giang' LIMIT 1), -- Chỉnh lại nếu slug Hà Nội khác
    (SELECT locationid FROM locations WHERE slug = 'quang-ninh' LIMIT 1),
    'Vịnh Hạ Long, Hang Sửng Sốt, Đảo Ti Tốp, Hang Luồn',
    '3 bữa chính + 1 bữa sáng buffet',
    'Quanh năm',
    'Du thuyền 5 sao, Kayak',
    'Cặp đôi, Gia đình, Khách nước ngoài',
    'Du thuyền Paradise Elegance',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Tour 2: Đà Nẵng -> Huế
INSERT INTO tours (
    tour_code, tour_name, duration, transportation,
    start_location_id, end_location_id,
    attractions, meals, ideal_time,
    trip_transportation, suitable_customer, hotel,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'TOUR-DN-HUE-01',
    'Khám phá cố đô Huế - Di sản văn hóa',
    '1 Ngày',
    'Xe du lịch 16 chỗ',
    (SELECT locationid FROM locations WHERE slug = 'da-nang' LIMIT 1),
    (SELECT locationid FROM locations WHERE slug = 'hue' LIMIT 1),
    'Đại Nội Huế, Chùa Thiên Mụ, Lăng Khải Định',
    'Ăn trưa đặc sản Huế',
    'Tháng 1 đến Tháng 8',
    'Xe ô tô, Thuyền rồng sông Hương',
    'Gia đình, Người lớn tuổi',
    'Không bao gồm (Tour trong ngày)',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Tour 3: TP.HCM -> Phú Quốc
INSERT INTO tours (
    tour_code, tour_name, duration, transportation,
    start_location_id, end_location_id,
    attractions, meals, ideal_time,
    trip_transportation, suitable_customer, hotel,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'TOUR-PQ-03',
    'Phú Quốc - Thiên đường Đảo Ngọc 3N2D',
    '3 Ngày 2 Đêm',
    'Máy bay Vietnam Airlines',
    (SELECT locationid FROM locations WHERE slug = 'ho-chi-minh' LIMIT 1),
    (SELECT locationid FROM locations WHERE slug = 'phu-quoc' LIMIT 1),
    'Bãi Sao, Hòn Thơm, Grand World, VinWonders',
    'Ăn sáng tại KS, 4 bữa chính hải sản',
    'Tháng 10 đến Tháng 4 năm sau',
    'Tàu cao tốc, Cáp treo',
    'Giới trẻ, Cặp đôi, Teambuilding',
    'Novotel Phú Quốc Resort 5*',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Tour 4: Hà Nội -> Hà Giang (Mùa hoa Tam Giác Mạch)
INSERT INTO tours (
    tour_code, tour_name, duration, transportation,
    start_location_id, end_location_id,
    attractions, meals, ideal_time,
    trip_transportation, suitable_customer, hotel,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'TOUR-HG-04',
    'Hà Giang - Mùa hoa Tam Giác Mạch Hùng Vĩ',
    '3 Ngày 2 Đêm',
    'Xe giường nằm chất lượng cao',
    (SELECT locationid FROM locations WHERE slug = 'ha-giang' LIMIT 1), -- Giả sử khởi hành từ TP Hà Giang hoặc chỉnh lại slug ha-noi
    (SELECT locationid FROM locations WHERE slug = 'ha-giang' LIMIT 1),
    'Cột cờ Lũng Cú, Đèo Mã Pí Lèng, Sông Nho Quế',
    'Đặc sản thắng cố, mèn mén',
    'Tháng 10, 11, 12',
    'Xe máy hoặc ô tô gầm cao',
    'Phượt thủ, Nhóm bạn trẻ, Nhiếp ảnh gia',
    'Homestay bản địa',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);