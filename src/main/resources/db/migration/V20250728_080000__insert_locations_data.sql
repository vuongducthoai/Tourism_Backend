-- Dữ liệu Miền Bắc (NORTH)
INSERT INTO locations (name, slug, region) VALUES
('Hà Giang', 'ha-giang', 'NORTH'),
('Quảng Ninh', 'quang-ninh', 'NORTH'),
('Lào Cai', 'lao-cai', 'NORTH'),
('Ninh Bình', 'ninh-binh', 'NORTH'),
('Yên Bái', 'yen-bai', 'NORTH');

-- Dữ liệu Miền Trung (CENTRAL)
INSERT INTO locations (name, slug, region) VALUES
('Đà Nẵng', 'da-nang', 'CENTRAL'),
('Huế', 'hue', 'CENTRAL'),
('Quảng Bình', 'quang-binh', 'CENTRAL'),
('Quy Nhơn', 'quy-nhon', 'CENTRAL'),
('Phú Yên', 'phu-yen', 'CENTRAL');

-- Dữ liệu Miền Đông Nam Bộ (Gộp vào SOUTH)
INSERT INTO locations (name, slug, region) VALUES
('Tây Ninh', 'tay-ninh', 'SOUTH'),
('TP. Hồ Chí Minh', 'ho-chi-minh', 'SOUTH'),
('Đồng Nai', 'dong-nai', 'SOUTH'),
('Bà Rịa - Vũng Tàu', 'ba-ria-vung-tau', 'SOUTH'),
('Côn Đảo', 'con-dao', 'SOUTH');

-- Dữ liệu Miền Tây Nam Bộ (Gộp vào SOUTH)
INSERT INTO locations (name, slug, region) VALUES
('Phú Quốc', 'phu-quoc', 'SOUTH'),
('Cần Thơ', 'can-tho', 'SOUTH'),
('Cà Mau', 'ca-mau', 'SOUTH'),
('Bạc Liêu', 'bac-lieu', 'SOUTH'),
('Tiền Giang', 'tien-giang', 'SOUTH');