UPDATE policy_templates
SET
    child_pricing_notes = '<ul>
        <li><strong>Trẻ em dưới 5 tuổi:</strong> Miễn phí (bố mẹ tự lo chi phí phát sinh).</li>
        <li><strong>Từ 5-10 tuổi:</strong> 75% giá vé (ngủ chung giường với bố mẹ).</li>
        <li><strong>Từ 11 tuổi trở lên:</strong> 100% giá vé như người lớn.</li>
    </ul>',
    payment_conditions = '<ul>
        <li><strong>Lần 1:</strong> Đặt cọc 50% ngay khi đăng ký.</li>
        <li><strong>Lần 2:</strong> Thanh toán 50% còn lại trước <strong>05 ngày</strong> khởi hành.</li>
    </ul>',
    registration_conditions = '<p>Quý khách vui lòng cung cấp đầy đủ hình ảnh <strong>CMND/CCCD</strong> hoặc <strong>Giấy khai sinh</strong> (đối với trẻ em) khi đăng ký tour để làm thủ tục bảo hiểm.</p>',
    regular_day_cancellation_rules = '<ul>
        <li>Hủy trước 20 ngày: <span style="color:green">Miễn phí</span>.</li>
        <li>Hủy từ 10-19 ngày: Phí <strong>30%</strong>.</li>
        <li>Hủy từ 5-9 ngày: Phí <strong>50%</strong>.</li>
        <li>Hủy dưới 5 ngày: Phí <strong>100%</strong>.</li>
    </ul>',
    holiday_cancellation_rules = '<p style="color:red"><strong>Không áp dụng hoàn hủy</strong> đối với các tour khởi hành dịp Lễ, Tết.</p>',
    force_majeure_rules = '<p>Bảo lưu tiền cọc trong vòng <strong>365 ngày</strong> nếu tour bị hủy do thiên tai, dịch bệnh hoặc lệnh cấm của nhà nước.</p>',
    packing_list = '<ul>
        <li>Mang theo giấy tờ tùy thân gốc (CMND/CCCD).</li>
        <li>Giày thể thao thoải mái để di chuyển nhiều.</li>
        <li>Thuốc đặc trị cá nhân, kem chống nắng, mũ nón.</li>
    </ul>'
WHERE template_name = 'Chính sách Hoàn Hủy Linh Hoạt (Nội Địa)';


-- 2. CHÍNH SÁCH TOUR BAY & BIỂN ĐẢO
UPDATE policy_templates
SET
    child_pricing_notes = '<ul>
        <li><strong>Dưới 2 tuổi:</strong> 10% giá vé (thuế phí sân bay).</li>
        <li><strong>Từ 2-11 tuổi:</strong> 90% giá vé.</li>
        <li><strong>12 tuổi trở lên:</strong> 100% giá vé.</li>
    </ul>',
    payment_conditions = '<p>Thanh toán <strong>100%</strong> ngay khi xuất vé máy bay. Vé máy bay không hoàn/hủy sau khi xuất.</p>',
    registration_conditions = '<p style="color:red">Họ tên đăng ký phải trùng khớp <strong>100%</strong> với giấy tờ tùy thân đi máy bay. Mọi sai sót khách hàng tự chịu trách nhiệm phí đổi tên.</p>',
    regular_day_cancellation_rules = '<ul>
        <li><strong>Vé máy bay:</strong> Mất 100% phí sau khi xuất vé.</li>
        <li><strong>Dịch vụ mặt đất:</strong> Hoàn lại 80% nếu báo trước 15 ngày khởi hành.</li>
    </ul>',
    holiday_cancellation_rules = '<p>Không hoàn tiền trong mọi trường hợp (Do tính chất vé máy bay khuyến mãi dịp lễ).</p>',
    force_majeure_rules = '<p>Hỗ trợ dời ngày bay (có thể thu phí chênh lệch vé) theo quy định của Hãng hàng không.</p>',
    packing_list = '<ul>
        <li>Kem chống nắng, kính râm, đồ bơi, mũ rộng vành.</li>
        <li>CCCD/Hộ chiếu còn hạn trên 6 tháng (bắt buộc để bay).</li>
    </ul>'
WHERE template_name = 'Chính sách Tour Bay & Biển Đảo';


-- 3. CHÍNH SÁCH MẠO HIỂM / VÙNG CAO
UPDATE policy_templates
SET
    child_pricing_notes = '<ul>
        <li>Không khuyến khích trẻ em dưới 5 tuổi tham gia.</li>
        <li><strong>Trẻ em 5-10 tuổi:</strong> 75% giá vé.</li>
    </ul>',
    payment_conditions = '<ul>
        <li>Cọc <strong>1.000.000 VNĐ</strong> để giữ chỗ.</li>
        <li>Hoàn tất thanh toán trước 3 ngày khởi hành.</li>
    </ul>',
    registration_conditions = '<p>Khách hàng cam kết có đủ sức khỏe. Người có tiền sử <strong>tim mạch, huyết áp cao</strong> cần báo trước với HDV.</p>',
    regular_day_cancellation_rules = '<ul>
        <li>Hủy trước 7 ngày: Miễn phí.</li>
        <li>Hủy trước 3 ngày: Phí 50%.</li>
        <li>Hủy trong vòng 24h: Phí 100%.</li>
    </ul>',
    holiday_cancellation_rules = '<p>Phí hủy 50% nếu báo trước 15 ngày. Sau đó là 100%.</p>',
    force_majeure_rules = '<p>Hoàn tiền <strong>100%</strong> nếu đường bị sạt lở hoặc điều kiện thời tiết không thể di chuyển.</p>',
    packing_list = '<ul>
        <li>Áo ấm (đặc biệt vào mùa đông).</li>
        <li>Giày leo núi hoặc giày có độ bám tốt.</li>
        <li>Thuốc chống côn trùng.</li>
        <li>Tiền mặt (do vùng cao ít cây ATM).</li>
    </ul>'
WHERE template_name = 'Chính sách Tour Khám Phá & Mạo Hiểm';


-- 4. CHÍNH SÁCH KHUYẾN MÃI (NON-REFUNDABLE)
UPDATE policy_templates
SET
    child_pricing_notes = '<p>Trẻ em tính giá bằng người lớn (do số lượng vé Khuyến Mãi có hạn).</p>',
    payment_conditions = '<p style="color:red; font-weight:bold">Thanh toán 100% ngay khi xác nhận đặt chỗ.</p>',
    registration_conditions = '<p>Không áp dụng đổi tên, không đổi ngày khởi hành sau khi đã xuất vé.</p>',
    regular_day_cancellation_rules = '<p style="color:red">Vé khuyến mãi không áp dụng hoàn tiền trong mọi trường hợp hủy tour.</p>',
    holiday_cancellation_rules = '<p>Không áp dụng hoàn hủy.</p>',
    force_majeure_rules = '<p>Được bảo lưu định danh sang tour khác trong vòng 3 tháng nếu có thiên tai.</p>',
    packing_list = '<p>Hành lý gọn nhẹ tối đa 7kg.</p>'
WHERE template_name = 'Chính sách Vé Khuyến Mãi (Không Hoàn Hủy)';