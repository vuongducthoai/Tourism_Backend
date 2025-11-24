-- =================================================================================
-- 1. CHÍNH SÁCH CHUẨN (HOÀN HỦY LINH HOẠT) - Áp dụng cho đa số Tour Miền Bắc
-- Liên hệ: Trụ sở Hà Nội
-- =================================================================================
INSERT INTO policy_templates (
    template_name,
    child_pricing_notes,
    payment_conditions,
    registration_conditions,
    regular_day_cancellation_rules,
    holiday_cancellation_rules,
    force_majeure_rules,
    packing_list,
    contact_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Chính sách Hoàn Hủy Linh Hoạt (Nội Địa)',
    'Trẻ em dưới 5 tuổi: Miễn phí (bố mẹ tự lo). Từ 5-10 tuổi: 75% giá vé (ngủ chung). Từ 11 tuổi: 100% giá vé.',
    'Đặt cọc 50% ngay khi đăng ký. Thanh toán 50% còn lại trước 05 ngày khởi hành.',
    'Cung cấp đầy đủ CMND/CCCD hoặc Giấy khai sinh (cho trẻ em) khi đăng ký.',
    'Hủy trước 20 ngày: Miễn phí. Hủy từ 10-19 ngày: Phí 30%. Hủy từ 5-9 ngày: Phí 50%. Hủy dưới 5 ngày: 100%.',
    'Không áp dụng hoàn hủy đối với các tour khởi hành dịp Lễ, Tết.',
    'Bảo lưu tiền cọc trong vòng 365 ngày nếu tour bị hủy do thiên tai, dịch bệnh hoặc lệnh cấm của nhà nước.',
    'Mang theo giấy tờ tùy thân gốc. Giày thể thao thoải mái. Thuốc đặc trị cá nhân.',
    (SELECT contactid FROM branch_contacts WHERE email = 'support.hn@tourism.com' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- 2. CHÍNH SÁCH TOUR BAY & BIỂN ĐẢO (KHẮT KHE HƠN) - Áp dụng cho Phú Quốc/Côn Đảo
-- Liên hệ: Văn phòng TP.HCM
-- =================================================================================
INSERT INTO policy_templates (
    template_name,
    child_pricing_notes,
    payment_conditions,
    registration_conditions,
    regular_day_cancellation_rules,
    holiday_cancellation_rules,
    force_majeure_rules,
    packing_list,
    contact_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Chính sách Tour Bay & Biển Đảo',
    'Trẻ em dưới 2 tuổi: 10% giá vé (thuế phí sân bay). Từ 2-11 tuổi: 90% giá vé. 12 tuổi trở lên: 100%.',
    'Thanh toán 100% ngay khi xuất vé máy bay. Vé máy bay không hoàn/hủy sau khi xuất.',
    'Họ tên đăng ký phải trùng khớp 100% với giấy tờ tùy thân đi máy bay.',
    'Hủy tour sau khi xuất vé: Mất 100% tiền vé máy bay. Phần dịch vụ mặt đất hoàn lại 80% nếu báo trước 15 ngày.',
    'Không hoàn tiền trong mọi trường hợp (Do tính chất vé máy bay khuyến mãi dịp lễ).',
    'Hỗ trợ dời ngày bay (có thu phí chênh lệch vé nếu có) theo quy định của Hãng hàng không.',
    'Kem chống nắng, kính râm, đồ bơi, mũ rộng vành. CCCD/Hộ chiếu còn hạn trên 6 tháng.',
    (SELECT contactid FROM branch_contacts WHERE email = 'support.hcm@tourism.com' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- 3. CHÍNH SÁCH MẠO HIỂM / VÙNG CAO - Áp dụng cho Hà Giang
-- Liên hệ: Trụ sở Hà Nội
-- =================================================================================
INSERT INTO policy_templates (
    template_name,
    child_pricing_notes,
    payment_conditions,
    registration_conditions,
    regular_day_cancellation_rules,
    holiday_cancellation_rules,
    force_majeure_rules,
    packing_list,
    contact_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Chính sách Tour Khám Phá & Mạo Hiểm',
    'Không khuyến khích trẻ em dưới 5 tuổi. Trẻ em 5-10 tuổi: 75% giá vé.',
    'Cọc 1.000.000 VNĐ giữ chỗ. Hoàn tất thanh toán trước 3 ngày khởi hành.',
    'Khách hàng cam kết có đủ sức khỏe. Người có tiền sử tim mạch, huyết áp cao cần báo trước.',
    'Hủy trước 7 ngày: Miễn phí. Hủy trước 3 ngày: Phí 50%. Hủy trong vòng 24h: 100%.',
    'Phí hủy 50% nếu báo trước 15 ngày. Sau đó là 100%.',
    'Hoàn tiền 100% nếu đường bị sạt lở không thể di chuyển.',
    'Áo ấm (mùa đông), giày leo núi chống trượt, thuốc chống côn trùng, tiền mặt (ít ATM).',
    (SELECT contactid FROM branch_contacts WHERE email = 'support.hn@tourism.com' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- =================================================================================
-- 4. CHÍNH SÁCH KHUYẾN MÃI (NON-REFUNDABLE) - Áp dụng cho các deal giá sốc
-- Liên hệ: Văn phòng Đà Nẵng
-- =================================================================================
INSERT INTO policy_templates (
    template_name,
    child_pricing_notes,
    payment_conditions,
    registration_conditions,
    regular_day_cancellation_rules,
    holiday_cancellation_rules,
    force_majeure_rules,
    packing_list,
    contact_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'Chính sách Vé Khuyến Mãi (Không Hoàn Hủy)',
    'Trẻ em tính giá bằng người lớn (do số lượng vé KM có hạn).',
    'Thanh toán 100% ngay khi xác nhận đặt chỗ.',
    'Không áp dụng đổi tên, không đổi ngày khởi hành.',
    'Vé khuyến mãi không áp dụng hoàn tiền trong mọi trường hợp hủy tour.',
    'Không áp dụng hoàn hủy.',
    'Được bảo lưu định danh sang tour khác trong vòng 3 tháng.',
    'Hành lý gọn nhẹ tối đa 7kg.',
    (SELECT contactid FROM branch_contacts WHERE email = 'support.dn@tourism.com' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);