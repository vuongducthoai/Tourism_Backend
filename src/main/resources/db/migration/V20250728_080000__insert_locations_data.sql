-- =================================================================================
-- INSERT DỮ LIỆU ĐỊA ĐIỂM (LOCATIONS)
-- Đã thêm URL ảnh đại diện cho mỗi địa điểm
-- =================================================================================

-- 1. MIỀN BẮC (NORTH)
INSERT INTO locations (name, slug, region, description, image, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
                                                                                                                               (
                                                                                                                                   'Hà Giang', 'ha-giang', 'NORTH',
                                                                                                                                   'Vùng đất địa đầu Tổ quốc, nổi tiếng với những cung đường đèo mãnh liệt, mùa hoa tam giác mạch thơ mộng và văn hóa đặc sắc của đồng bào dân tộc vùng cao.',
                                                                                                                                   'https://i.imgur.com/vkJqXu8.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Quảng Ninh', 'quang-ninh', 'NORTH',
                                                                                                                                   'Vùng đất mỏ giàu truyền thống, sở hữu Vịnh Hạ Long - Di sản thiên nhiên thế giới hai lần được UNESCO công nhận với hàng nghìn hòn đảo đá vôi kỳ vĩ.',
                                                                                                                                   'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/quang-ninh.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Lào Cai', 'lao-cai', 'NORTH',
                                                                                                                                   'Nổi tiếng với thị trấn Sapa mờ sương, đỉnh Fansipan - Nóc nhà Đông Dương và những thửa ruộng bậc thang tuyệt đẹp mùa lúa chín.',
                                                                                                                                   'https://media.istockphoto.com/id/1182620587/photo/panoramic-view-of-ha-long-bay-vietnam.webp?a=1&b=1&s=612x612&w=0&k=20&c=MEDp6uGIkMbB50pGvwvJ2LdR82_g_Q5uFYPY1lv0XfA=', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Ninh Bình', 'ninh-binh', 'NORTH',
                                                                                                                                   'Được ví như Vịnh Hạ Long trên cạn, nổi tiếng với Quần thể danh thắng Tràng An, Tam Cốc - Bích Động và cố đô Hoa Lư lịch sử.',
                                                                                                                                   'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/ninh-binh.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Yên Bái', 'yen-bai', 'NORTH',
                                                                                                                                   'Điểm đến của những thửa ruộng bậc thang Mù Cang Chải vàng óng mùa lúa chín và đèo Khau Phạ - một trong tứ đại đỉnh đèo Tây Bắc.',
                                                                                                                                   'https://images.unsplash.com/photo-1731051083752-400d9df485e9?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bCVDMyVBMG8lMjBjYWl8ZW58MHx8MHx8fDA%3D', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               );

-- 2. MIỀN TRUNG (CENTRAL)
INSERT INTO locations (name, slug, region, description, image, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
                                                                                                                               (
                                                                                                                                   'Đà Nẵng', 'da-nang', 'CENTRAL',
                                                                                                                                   'Thành phố đáng sống nhất Việt Nam, điểm đến của những cây cầu độc đáo, bãi biển Mỹ Khê quyến rũ và khu du lịch Bà Nà Hills đẳng cấp.',
                                                                                                                                   'https://images.unsplash.com/photo-1531737212413-667205e1cda7?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8TmluaCUyMEIlQzMlQUNuaHxlbnwwfHwwfHx8MA%3D%3D', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Huế', 'hue', 'CENTRAL',
                                                                                                                                   'Cố đô mộng mơ bên dòng sông Hương, nơi lưu giữ những giá trị văn hóa cung đình, kiến trúc lăng tẩm triều Nguyễn và Nhã nhạc cung đình Huế.',
                                                                                                                                   'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/hue.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Quảng Bình', 'quang-binh', 'CENTRAL',
                                                                                                                                   'Vương quốc hang động, nơi có Vườn quốc gia Phong Nha - Kẻ Bàng và hang Sơn Đoòng lớn nhất thế giới, điểm đến lý tưởng cho du lịch mạo hiểm.',
                                                                                                                                   'https://media.istockphoto.com/id/2213856586/photo/asian-woman-wearing-vietnam-culture-traditional-in-rice-terrace-at-mu-cang-chai-vietnam.webp?a=1&b=1&s=612x612&w=0&k=20&c=sxiUvULVU4ZyteLdYP2Vn2HGtm-Pajjy8x4Dqsuy5mY=', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Quy Nhơn', 'quy-nhon', 'CENTRAL',
                                                                                                                                   'Thành phố biển bình yên với những bãi tắm hoang sơ như Kỳ Co, Eo Gió, được mệnh danh là Maldives của Việt Nam.',
                                                                                                                                   'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/quy-nhon.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               ),
                                                                                                                               (
                                                                                                                                   'Phú Yên', 'phu-yen', 'CENTRAL',
                                                                                                                                   'Vùng đất "Hoa vàng trên cỏ xanh" với Ghềnh Đá Đĩa độc đáo, Mũi Điện - nơi đón bình minh đầu tiên trên đất liền Việt Nam.',
                                                                                                                                   'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/phu-yen.jpg', -- ✨ Thêm URL ảnh
                                                                                                                                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
                                                                                                                               );

-- 3. MIỀN NAM (SOUTH - Bao gồm Đông & Tây Nam Bộ)
INSERT INTO locations (name, slug, region, description, image, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
-- Đông Nam Bộ
(
    'Tây Ninh', 'tay-ninh', 'SOUTH',
    'Vùng đất thánh của đạo Cao Đài, nổi tiếng với Núi Bà Đen cao nhất Nam Bộ và hồ Dầu Tiếng thơ mộng.',
    'https://images.unsplash.com/photo-1758178673657-88700bbeda3c?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTR8fFMlQzYlQTFuJTIwTGF8ZW58MHx8MHx8fDA%3D', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'TP. Hồ Chí Minh', 'ho-chi-minh', 'SOUTH',
    'Trung tâm kinh tế, văn hóa lớn nhất cả nước, sôi động, hiện đại nhưng vẫn giữ được những nét kiến trúc cổ kính và ẩm thực đường phố đa dạng.',
    'https://images.unsplash.com/photo-1691311861289-baa275fddfa1?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjV8fENhbyUyMEIlRTElQkElQjFuZ3xlbnwwfHwwfHx8MA%3D%3D', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Đồng Nai', 'dong-nai', 'SOUTH',
    'Cửa ngõ miền Đông Nam Bộ, nổi tiếng với Vườn quốc gia Cát Tiên, khu du lịch Bửu Long và thác Giang Điền.',
    'https://images.unsplash.com/photo-1675055959114-7a3e8c0a22d4?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8SCVFMSVCQSVBM2klMjBQaCVDMyVCMm5nfGVufDB8fDB8fHww', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Bà Rịa - Vũng Tàu', 'ba-ria-vung-tau', 'SOUTH',
    'Thành phố biển nghỉ dưỡng nổi tiếng gần Sài Gòn với bãi Sau, bãi Trước, tượng Chúa Kitô Vua và ngọn hải đăng cổ.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/vung-tau.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Côn Đảo', 'con-dao', 'SOUTH',
    'Di tích lịch sử hào hùng kết hợp với thiên nhiên hoang sơ, bãi biển tuyệt đẹp và hệ sinh thái biển đa dạng.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/con-dao.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
-- Tây Nam Bộ
(
    'Phú Quốc', 'phu-quoc', 'SOUTH',
    'Đảo Ngọc thiên đường với những bãi biển hoang sơ, nước xanh ngọc bích, hệ thống nghỉ dưỡng đẳng cấp và đặc sản hồ tiêu, nước mắm.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/phu-quoc.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Cần Thơ', 'can-tho', 'SOUTH',
    'Thủ phủ miền Tây sông nước, nổi tiếng với vẻ đẹp bình dị của Chợ nổi Cái Răng, bến Ninh Kiều và những vườn trái cây trĩu quả.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/can-tho.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Cà Mau', 'ca-mau', 'SOUTH',
    'Vùng đất cực Nam Tổ quốc, nổi tiếng với hệ sinh thái rừng ngập mặn, cột mốc tọa độ quốc gia và đặc sản cua Cà Mau.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/ca-mau.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Bạc Liêu', 'bac-lieu', 'SOUTH',
    'Vùng đất của Công tử Bạc Liêu, nổi tiếng với cánh đồng điện gió khổng lồ, vườn nhãn cổ và nghệ thuật Đờn ca tài tử.',
    'https://res.cloudinary.com/dt9g8c98k/image/upload/v1/locations/bac-lieu.jpg', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
),
(
    'Tiền Giang', 'tien-giang', 'SOUTH',
    'Vựa trái cây lớn nhất miền Tây, nổi tiếng với Chùa Vĩnh Tràng, trại rắn Đồng Tâm và các cù lao xanh mát trên sông Tiền.',
    'https://images.unsplash.com/photo-1651108143947-3290f61c037e?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8aCVDMyVBMCUyMG4lRTElQkIlOTlpfGVufDB8fDB8fHww', -- ✨ Thêm URL ảnh
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);