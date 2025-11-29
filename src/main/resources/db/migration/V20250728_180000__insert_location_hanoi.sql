-- =================================================================================
-- INSERT THÊM ĐỊA ĐIỂM: HÀ NỘI
-- (Bổ sung để làm điểm khởi hành cho các Tour Miền Bắc)
-- =================================================================================

INSERT INTO locations (
    name,
    slug,
    region,
    description,
    image,
    airport_code,
    airport_name,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Hà Nội',
    'ha-noi',
    'NORTH',
    'Thủ đô ngàn năm văn hiến, trái tim của cả nước. Nổi tiếng với 36 phố phường, Hồ Gươm, Lăng Bác và nền ẩm thực đường phố tinh tế, đậm đà bản sắc dân tộc.',
    'https://images.unsplash.com/photo-1555921090-b3a8370e61e3?q=80&w=2070&auto=format&fit=crop', -- Ảnh Hồ Gươm
    'HAN',
    'Sân bay quốc tế Nội Bài',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);