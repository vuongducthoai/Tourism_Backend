-- 1. Thêm cột description (Dùng IF NOT EXISTS để tránh lỗi nếu lỡ chạy rồi)
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS description TEXT;

-- 2. Cập nhật dữ liệu cho các mã đã tồn tại
UPDATE coupons
SET description = 'Giảm ngay 200k cho thành viên mới đăng ký lần đầu. Áp dụng cho mọi tour.'
WHERE coupon_code = 'WELCOME2025';

UPDATE coupons
SET description = 'Ưu đãi đặc biệt giảm 500k cho đơn hàng có giá trị từ 5.000.000đ trở lên.'
WHERE coupon_code = 'BIGSALE500';

UPDATE coupons
SET description = 'Xả vé giờ chót Tour Hạ Long khởi hành ngày 02/09/2025. Số lượng có hạn.'
WHERE coupon_code = 'HL0209LASTMINUTE';

UPDATE coupons
SET description = 'Kích cầu du lịch Huế - Giảm 100k cho tour khởi hành ngày 15/08/2025.'
WHERE coupon_code = 'HUE1508VIP';

UPDATE coupons
SET description = 'Mã giảm giá năm 2023 (Đã hết hạn sử dụng).'
WHERE coupon_code = 'EXPIRED2023';