ALTER TABLE locations
ADD COLUMN airport_name VARCHAR(255);

UPDATE locations SET airport_name = 'Sân bay quốc tế Nội Bài' WHERE airport_code = 'HAN';
UPDATE locations SET airport_name = 'Sân bay quốc tế Vân Đồn' WHERE airport_code = 'VDO';
UPDATE locations SET airport_name = 'Sân bay Cát Bi' WHERE airport_code = 'HPH'; -- Hải Phòng (nếu có)

UPDATE locations SET airport_name = 'Sân bay quốc tế Đà Nẵng' WHERE airport_code = 'DAD';
UPDATE locations SET airport_name = 'Sân bay quốc tế Phú Bài' WHERE airport_code = 'HUI'; -- Huế
UPDATE locations SET airport_name = 'Sân bay Đồng Hới' WHERE airport_code = 'VDH'; -- Quảng Bình
UPDATE locations SET airport_name = 'Sân bay Phù Cát' WHERE airport_code = 'UIH'; -- Quy Nhơn
UPDATE locations SET airport_name = 'Sân bay Tuy Hòa' WHERE airport_code = 'TBB'; -- Phú Yên

UPDATE locations SET airport_name = 'Sân bay quốc tế Tân Sơn Nhất' WHERE airport_code = 'SGN';
UPDATE locations SET airport_name = 'Sân bay quốc tế Phú Quốc' WHERE airport_code = 'PQC';
UPDATE locations SET airport_name = 'Sân bay quốc tế Cần Thơ' WHERE airport_code = 'VCA';
UPDATE locations SET airport_name = 'Sân bay Côn Đảo' WHERE airport_code = 'VCS';
UPDATE locations SET airport_name = 'Sân bay Rạch Giá' WHERE airport_code = 'VKG'; -- Kiên Giang

