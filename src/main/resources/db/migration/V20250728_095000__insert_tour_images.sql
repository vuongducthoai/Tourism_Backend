-- =================================================================================
-- INSERT HÌNH ẢNH CHO CÁC TOUR
-- Logic: Mỗi tour sẽ có 1 ảnh là MAIN (is_main_image = true), còn lại là false
-- =================================================================================

-- 1. TOUR HẠ LONG (TOUR-HL-01) - 5 Ảnh
-- Ảnh 1 (Ảnh chính)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES ('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972312/HL1_uyf51t.jpg', true, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);

-- Ảnh 2, 3, 4, 5 (Ảnh phụ)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972311/HL3_apcswv.jpg', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972311/HL4_y1jkyf.jpg', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972311/HL4_y1jkyf.jpg', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972311/HL4_y1jkyf.jpg', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);


-- 2. TOUR ĐÀ NẴNG - HUẾ (TOUR-DN-HUE-01) - 3 Ảnh
-- Ảnh 1 (Ảnh chính)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES ('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972461/Hue01_luakh8.webp', true, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);

-- Ảnh 2, 3 (Ảnh phụ)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972460/HUE03_ekntkc.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972459/HUE02_jasjzh.jpg', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);


-- 3. TOUR PHÚ QUỐC (TOUR-PQ-03) - 3 Ảnh
-- Ảnh 1 (Ảnh chính)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES ('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972552/PQ_edzdrg.webp', true, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);

-- Ảnh 2, 3 (Ảnh phụ)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972551/PQ2_jx3yt9.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972547/PQ3_s7l32j.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);


-- 4. TOUR HÀ GIANG (TOUR-HG-04) - 6 Ảnh
-- Ảnh 1 (Ảnh chính)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES ('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972679/HG01_ctg3og.webp', true, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);

-- Ảnh 2, 3, 4, 5, 6 (Ảnh phụ)
INSERT INTO tour_images (image_url, is_main_image, tour_id, created_at, updated_at, created_by, updated_by, is_deleted) VALUES
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972678/HG02_wfdl2f.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972677/HG03_mf870n.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972676/HG04_sxnddy.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972675/HG05_k0tnf5.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false),
('https://res.cloudinary.com/dnt8vx1at/image/upload/v1763972674/hg06_xsaabt.webp', false, (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false);