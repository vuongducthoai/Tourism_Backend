-- 2. Cập nhật mã sân bay cho các địa điểm
UPDATE locations SET airport_code = 'SGN' WHERE slug = 'ho-chi-minh'; -- Tân Sơn Nhất
UPDATE locations SET airport_code = 'HAN' WHERE slug = 'ha-noi';      -- Nội Bài
UPDATE locations SET airport_code = 'DAD' WHERE slug = 'da-nang';     -- Đà Nẵng
UPDATE locations SET airport_code = 'PQC' WHERE slug = 'phu-quoc';    -- Phú Quốc
UPDATE locations SET airport_code = 'HUI' WHERE slug = 'hue';         -- Phú Bài
UPDATE locations SET airport_code = 'VCA' WHERE slug = 'can-tho';     -- Cần Thơ
UPDATE locations SET airport_code = 'VDO' WHERE slug = 'quang-ninh';  -- Vân Đồn
UPDATE locations SET airport_code = 'VDH' WHERE slug = 'quang-binh';  -- Đồng Hới
UPDATE locations SET airport_code = 'TBB' WHERE slug = 'phu-yen';     -- Tuy Hòa
UPDATE locations SET airport_code = 'UIH' WHERE slug = 'quy-nhon';    -- Phù Cát
UPDATE locations SET airport_code = 'VCS' WHERE slug = 'con-dao';     -- Côn Đảo
