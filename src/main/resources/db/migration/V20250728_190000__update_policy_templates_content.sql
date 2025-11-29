ALTER TABLE policy_templates ADD COLUMN IF NOT EXISTS tour_price_includes TEXT;
ALTER TABLE policy_templates ADD COLUMN IF NOT EXISTS tour_price_excludes TEXT;ALTER TABLE policy_templates ADD COLUMN IF NOT EXISTS tour_price_includes TEXT;
ALTER TABLE policy_templates ADD COLUMN IF NOT EXISTS tour_price_excludes TEXT;

UPDATE policy_templates
SET
    tour_price_includes = '<ul>
        <li>Xe ô tô du lịch đời mới, máy lạnh đưa đón theo chương trình.</li>
        <li>Khách sạn tiêu chuẩn 3-4 sao (02 khách/phòng, lẻ nam/nữ ngủ 3).</li>
        <li>Các bữa ăn theo chương trình (Sáng, Trưa, Tối).</li>
        <li>Vé tham quan các điểm trong lịch trình (vé cửa 1).</li>
        <li>Nước uống trên xe (1 chai/người/ngày), khăn lạnh.</li>
        <li>Bảo hiểm du lịch nội địa mức bồi thường tối đa 20.000.000 VNĐ.</li>
    </ul>',
    tour_price_excludes = '<ul>
        <li>Thuế VAT (8-10%).</li>
        <li>Chi phí cá nhân: Giặt ủi, điện thoại, đồ uống trong các bữa ăn.</li>
        <li>Chi phí phòng đơn (nếu quý khách có nhu cầu ngủ riêng).</li>
        <li>Tiền TIP cho Hướng dẫn viên và Tài xế (tùy tâm).</li>
    </ul>',
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




UPDATE policy_templates
SET
    tour_price_includes = '<ul>
        <li>Vé máy bay khứ hồi (bao gồm hành lý xách tay 7kg).</li>
        <li>Xe đưa đón sân bay và tham quan tại đảo.</li>
        <li>Tàu cao tốc/Cano di chuyển ra đảo (nếu có).</li>
        <li>Lưu trú tại Resort/Khách sạn gần biển.</li>
        <li>Dụng cụ lặn ngắm san hô cơ bản (áo phao, kính lặn).</li>
    </ul>',
    tour_price_excludes = '<ul>
        <li>Hành lý ký gửi phát sinh ngoài quy định vé máy bay.</li>
        <li>Chi phí các trò chơi trên biển: Dù lượn, mô tô nước, lặn bình khí.</li>
        <li>Phụ thu phòng đơn, phụ thu dịp Lễ/Tết.</li>
        <li>Xe vận chuyển ngoài chương trình.</li>
    </ul>',
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


UPDATE policy_templates
SET
    tour_price_includes = '<ul>
        <li>Xe địa hình chuyên dụng hoặc xe Limousine đi vùng cao.</li>
        <li>Homestay bản địa hoặc lều trại tiêu chuẩn dã ngoại.</li>
        <li>Các bữa ăn đặc sản địa phương.</li>
        <li>Hướng dẫn viên chuyên tuyến, có kinh nghiệm sơ cứu.</li>
        <li>Porter (người khuân vác) hỗ trợ mang đồ chung (đối với tour Trekking).</li>
        <li>Giấy phép tham quan vùng biên giới/rừng quốc gia.</li>
    </ul>',
    tour_price_excludes = '<ul>
        <li>Tiền bồi dưỡng (Tip) bắt buộc cho Porter và HDV bản địa.</li>
        <li>Chi phí tắm lá thuốc, massage chân (nếu có).</li>
        <li>Trang thiết bị cá nhân chuyên dụng: Giày trekking, gậy leo núi cá nhân.</li>
    </ul>',
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


UPDATE policy_templates
SET
    tour_price_includes = '<ul>
        <li>Xe vận chuyển ghế ngồi mềm.</li>
        <li>Vé tham quan cổng chính.</li>
        <li>1 chai nước suối/khách.</li>
        <li>Bảo hiểm du lịch cơ bản.</li>
    </ul>',
    tour_price_excludes = '<ul>
        <li><strong>Ăn uống:</strong> Quý khách tự túc ăn uống hoặc đặt ăn theo đoàn (phụ thu).</li>
        <li>Thuế VAT.</li>
        <li>Mọi chi phí cá nhân và tham quan phát sinh.</li>
    </ul>',
    child_pricing_notes = '<p>Trẻ em tính giá bằng người lớn (do số lượng vé Khuyến Mãi có hạn).</p>',
    payment_conditions = '<p style="color:red; font-weight:bold">Thanh toán 100% ngay khi xác nhận đặt chỗ.</p>',
    registration_conditions = '<p>Không áp dụng đổi tên, không đổi ngày khởi hành sau khi đã xuất vé.</p>',
    regular_day_cancellation_rules = '<p style="color:red">Vé khuyến mãi không áp dụng hoàn tiền trong mọi trường hợp hủy tour.</p>',
    holiday_cancellation_rules = '<p>Không áp dụng hoàn hủy.</p>',
    force_majeure_rules = '<p>Được bảo lưu định danh sang tour khác trong vòng 3 tháng nếu có thiên tai.</p>',
    packing_list = '<p>Hành lý gọn nhẹ tối đa 7kg.</p>'
WHERE template_name = 'Chính sách Vé Khuyến Mãi (Không Hoàn Hủy)';

