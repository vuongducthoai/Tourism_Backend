-- =================================================================================
-- TOUR 1: Du thuyền Hạ Long 2N1D (Mã: TOUR-HL-01)
-- =================================================================================

-- Ngày 1
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    1,
    'Hà Nội - Tuần Châu - Vịnh Hạ Long - Hang Sửng Sốt',
    'Trưa, Tối',
    '08:00: Xe đón quý khách tại khu vực Phố Cổ Hà Nội khởi hành đi Hạ Long.
    12:00: Đến cảng Tuần Châu, làm thủ tục check-in lên du thuyền Paradise Elegance. Thưởng thức đồ uống chào mừng.
    13:00: Dùng bữa trưa buffet Á - Âu trong khi tàu di chuyển qua các hòn đảo lớn nhỏ.
    15:00: Thăm quan Hang Sửng Sốt - hang động đẹp và lớn nhất Vịnh Hạ Long.
    16:30: Tự do tắm biển tại đảo Ti Tốp hoặc leo lên đỉnh núi ngắm toàn cảnh Vịnh.
    19:00: Dùng bữa tối sang trọng trên du thuyền. Sau đó tham gia câu mực hoặc hát Karaoke.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Ngày 2
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    2,
    'Tai Chi - Hang Luồn - Hà Nội',
    'Sáng, Trưa (Brunch)',
    '06:00: Quý khách dậy sớm tập Thái Cực Quyền trên boong tàu và ngắm bình minh.
    07:00: Dùng điểm tâm sáng nhẹ.
    08:00: Chèo thuyền Kayak khám phá Hang Luồn, nơi có phong cảnh thiên nhiên hoang sơ.
    09:30: Quay lại tàu, làm thủ tục trả phòng.
    10:30: Dùng bữa trưa sớm (Brunch) trong khi tàu di chuyển về bến cảng.
    12:00: Tàu cập bến Tuần Châu, xe đón quý khách trở về Hà Nội.
    15:00: Về đến Hà Nội, kết thúc chương trình.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- TOUR 2: Khám phá cố đô Huế 1 Ngày (Mã: TOUR-DN-HUE-01)
-- =================================================================================

INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    1,
    'Đà Nẵng - Lăng Khải Định - Đại Nội - Chùa Thiên Mụ',
    'Trưa',
    '07:30: Xe và HDV đón khách tại khách sạn trung tâm Đà Nẵng, khởi hành đi Huế xuyên qua hầm Hải Vân.
    10:00: Tham quan Lăng Khải Định - công trình kiến trúc kết hợp Đông Tây độc đáo.
    12:00: Ăn trưa tại nhà hàng với đặc sản Huế (Bánh bèo, nậm, lọc...).
    13:30: Tham quan Đại Nội (Hoàng Thành Huế) - nơi đóng đô của triều đại nhà Nguyễn.
    15:30: Viếng Chùa Thiên Mụ cổ kính bên dòng sông Hương thơ mộng.
    16:30: Khởi hành về lại Đà Nẵng.
    18:30: Trả khách tại điểm đón ban đầu.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- TOUR 3: Phú Quốc 3N2D (Mã: TOUR-PQ-03)
-- =================================================================================

-- Ngày 1
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    1,
    'Đón sân bay - Check in Sunset Sanato - Grand World',
    'Tối',
    'Sáng: Xe đón quý khách tại sân bay Phú Quốc, đưa về khách sạn gửi hành lý.
    Chiều: Tham quan Sunset Sanato Beach Club - điểm ngắm hoàng hôn đẹp nhất Phú Quốc.
    Tối: Khám phá "Thành phố không ngủ" Grand World, xem show nhạc nước Sắc màu Venice. Nghỉ đêm tại Resort.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Ngày 2
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    2,
    'Tour cano 4 đảo - Cáp treo Hòn Thơm',
    'Sáng, Trưa, Tối',
    '08:00: Ăn sáng tại khách sạn.
    09:00: Lên cano tham quan 4 hòn đảo đẹp nhất: Hòn Mây Rút, Hòn Gầm Ghì, Hòn Móng Tay. Lặn ngắm san hô.
    12:00: Ăn trưa hải sản trên đảo.
    14:00: Trải nghiệm Cáp treo Hòn Thơm vượt biển dài nhất thế giới. Vui chơi tại công viên nước Aquatopia.
    18:00: Ăn tối tại chợ đêm Phú Quốc, tự do mua sắm.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Ngày 3
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    3,
    'Mua sắm đặc sản - Tiễn sân bay',
    'Sáng',
    '08:00: Ăn sáng, tự do tắm biển hoặc hồ bơi tại Resort.
    10:00: Làm thủ tục trả phòng.
    10:30: Tham quan cơ sở nuôi cấy Ngọc Trai, Vườn Tiêu, Nhà thùng nước mắm để mua quà.
    12:00: Xe đưa quý khách ra sân bay Phú Quốc. Kết thúc chương trình.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- TOUR 4: Hà Giang 3N2D (Mã: TOUR-HG-04)
-- =================================================================================

-- Ngày 1
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    1,
    'Hà Nội - Quản Bạ - Yên Minh',
    'Trưa, Tối',
    '06:00: Xe đón đoàn khởi hành đi Hà Giang.
    12:00: Ăn trưa tại TP Hà Giang. Chụp ảnh tại Cột mốc số 0.
    14:00: Dừng chân tại Cổng trời Quản Bạ, ngắm Núi Đôi Cô Tiên.
    17:00: Đến thị trấn Yên Minh, nhận phòng khách sạn.
    19:00: Ăn tối và nghỉ đêm tại Yên Minh.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Ngày 2
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    2,
    'Yên Minh - Lũng Cú - Sông Nho Quế - Đồng Văn',
    'Sáng, Trưa, Tối',
    '07:00: Ăn sáng, khởi hành đi Cao nguyên đá Đồng Văn.
    09:00: Thăm bản Sủng Là (Nhà của Pao), Dinh thự Vua Mèo.
    11:00: Chinh phục Cột cờ Lũng Cú - điểm cực Bắc tổ quốc.
    12:30: Ăn trưa tại Đồng Văn.
    14:00: Chinh phục đèo Mã Pí Lèng. Đi thuyền trên sông Nho Quế, đi qua Hẻm Tu Sản.
    18:00: Về thị trấn Đồng Văn, nhận phòng. Thưởng thức Cafe Phố Cổ.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- Ngày 3
INSERT INTO itinerary_days (
    day_number, title, meals, details, tour_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    3,
    'Chợ phiên Đồng Văn - Hà Nội',
    'Sáng, Trưa',
    '07:00: Quý khách tham gia Chợ phiên Đồng Văn (nếu vào Chủ Nhật), mua sắm đặc sản vùng cao.
    08:30: Khởi hành về lại Hà Nội.
    12:00: Ăn trưa tại TP Hà Giang.
    19:00: Về đến Hà Nội, chia tay đoàn.',
    (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);