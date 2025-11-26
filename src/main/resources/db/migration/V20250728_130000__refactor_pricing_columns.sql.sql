-- 1. Đổi tên cột 'money' thành 'sale_price' (Giữ nguyên dữ liệu cũ)
ALTER TABLE departure_pricing
RENAME COLUMN money TO sale_price;

-- 2. Thêm cột 'original_price' (Giá gốc)
ALTER TABLE departure_pricing
ADD COLUMN original_price DECIMAL(19, 2);

-- 3. Cập nhật dữ liệu giả lập cho 'original_price'
-- Logic: Giá gốc cao hơn giá bán 15% (Làm tròn đến hàng nghìn)
UPDATE departure_pricing
SET original_price = ROUND(sale_price * 1.15, -3);

-- 4. Xử lý ngoại lệ: Vé em bé (INFANT) thường không giảm giá -> Giá gốc = Giá bán
UPDATE departure_pricing
SET original_price = sale_price
WHERE passenger_type = 'INFANT';

-- 5. Đảm bảo ràng buộc: Giá gốc không được nhỏ hơn giá bán
UPDATE departure_pricing
SET original_price = sale_price
WHERE original_price < sale_price;