-- =================================================================================
-- CẬP NHẬT CHI TIẾT LỊCH TRÌNH SANG ĐỊNH DẠNG HTML
-- Mục đích: Hiển thị đẹp trên Frontend (Timeline, in đậm giờ)
-- =================================================================================

-- ---------------------------------------------------------------------------------
-- 1. TOUR HẠ LONG (TOUR-HL-01)
-- ---------------------------------------------------------------------------------

-- Ngày 1
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>08:00:</strong> Xe đón quý khách tại khu vực Phố Cổ Hà Nội khởi hành đi Hạ Long.</li>
    <li><strong>12:00:</strong> Đến cảng Tuần Châu, làm thủ tục check-in lên du thuyền Paradise Elegance. Thưởng thức đồ uống chào mừng.</li>
    <li><strong>13:00:</strong> Dùng bữa trưa buffet Á - Âu trong khi tàu di chuyển qua các hòn đảo lớn nhỏ.</li>
    <li><strong>15:00:</strong> Thăm quan Hang Sửng Sốt - hang động đẹp và lớn nhất Vịnh Hạ Long.</li>
    <li><strong>16:30:</strong> Tự do tắm biển tại đảo Ti Tốp hoặc leo lên đỉnh núi ngắm toàn cảnh Vịnh.</li>
    <li><strong>19:00:</strong> Dùng bữa tối sang trọng trên du thuyền. Sau đó tham gia câu mực hoặc hát Karaoke.</li>
</ul>'
WHERE day_number = 1
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01');

-- Ngày 2
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>06:00:</strong> Quý khách dậy sớm tập Thái Cực Quyền trên boong tàu và ngắm bình minh.</li>
    <li><strong>07:00:</strong> Dùng điểm tâm sáng nhẹ.</li>
    <li><strong>08:00:</strong> Chèo thuyền Kayak khám phá Hang Luồn, nơi có phong cảnh thiên nhiên hoang sơ.</li>
    <li><strong>09:30:</strong> Quay lại tàu, làm thủ tục trả phòng.</li>
    <li><strong>10:30:</strong> Dùng bữa trưa sớm (Brunch) trong khi tàu di chuyển về bến cảng.</li>
    <li><strong>12:00:</strong> Tàu cập bến Tuần Châu, xe đón quý khách trở về Hà Nội.</li>
    <li><strong>15:00:</strong> Về đến Hà Nội, kết thúc chương trình.</li>
</ul>'
WHERE day_number = 2
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HL-01');


-- ---------------------------------------------------------------------------------
-- 2. TOUR HUẾ (TOUR-DN-HUE-01)
-- ---------------------------------------------------------------------------------

-- Ngày 1
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>07:30:</strong> Xe và HDV đón khách tại khách sạn trung tâm Đà Nẵng, khởi hành đi Huế xuyên qua hầm Hải Vân.</li>
    <li><strong>10:00:</strong> Tham quan Lăng Khải Định - công trình kiến trúc kết hợp Đông Tây độc đáo.</li>
    <li><strong>12:00:</strong> Ăn trưa tại nhà hàng với đặc sản Huế (Bánh bèo, nậm, lọc...).</li>
    <li><strong>13:30:</strong> Tham quan Đại Nội (Hoàng Thành Huế) - nơi đóng đô của triều đại nhà Nguyễn.</li>
    <li><strong>15:30:</strong> Viếng Chùa Thiên Mụ cổ kính bên dòng sông Hương thơ mộng.</li>
    <li><strong>16:30:</strong> Khởi hành về lại Đà Nẵng.</li>
    <li><strong>18:30:</strong> Trả khách tại điểm đón ban đầu.</li>
</ul>'
WHERE day_number = 1
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-DN-HUE-01');


-- ---------------------------------------------------------------------------------
-- 3. TOUR PHÚ QUỐC (TOUR-PQ-03)
-- ---------------------------------------------------------------------------------

-- Ngày 1
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>Sáng:</strong> Xe đón quý khách tại sân bay Phú Quốc, đưa về khách sạn gửi hành lý.</li>
    <li><strong>Chiều:</strong> Tham quan Sunset Sanato Beach Club - điểm ngắm hoàng hôn đẹp nhất Phú Quốc.</li>
    <li><strong>Tối:</strong> Khám phá "Thành phố không ngủ" Grand World, xem show nhạc nước Sắc màu Venice. Nghỉ đêm tại Resort.</li>
</ul>'
WHERE day_number = 1
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03');

-- Ngày 2
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>08:00:</strong> Ăn sáng tại khách sạn.</li>
    <li><strong>09:00:</strong> Lên cano tham quan 4 hòn đảo đẹp nhất: Hòn Mây Rút, Hòn Gầm Ghì, Hòn Móng Tay. Lặn ngắm san hô.</li>
    <li><strong>12:00:</strong> Ăn trưa hải sản trên đảo.</li>
    <li><strong>14:00:</strong> Trải nghiệm Cáp treo Hòn Thơm vượt biển dài nhất thế giới. Vui chơi tại công viên nước Aquatopia.</li>
    <li><strong>18:00:</strong> Ăn tối tại chợ đêm Phú Quốc, tự do mua sắm.</li>
</ul>'
WHERE day_number = 2
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03');

-- Ngày 3
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>08:00:</strong> Ăn sáng, tự do tắm biển hoặc hồ bơi tại Resort.</li>
    <li><strong>10:00:</strong> Làm thủ tục trả phòng.</li>
    <li><strong>10:30:</strong> Tham quan cơ sở nuôi cấy Ngọc Trai, Vườn Tiêu, Nhà thùng nước mắm để mua quà.</li>
    <li><strong>12:00:</strong> Xe đưa quý khách ra sân bay Phú Quốc. Kết thúc chương trình.</li>
</ul>'
WHERE day_number = 3
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-PQ-03');


-- ---------------------------------------------------------------------------------
-- 4. TOUR HÀ GIANG (TOUR-HG-04)
-- ---------------------------------------------------------------------------------

-- Ngày 1
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>06:00:</strong> Xe đón đoàn khởi hành đi Hà Giang.</li>
    <li><strong>12:00:</strong> Ăn trưa tại TP Hà Giang. Chụp ảnh tại Cột mốc số 0.</li>
    <li><strong>14:00:</strong> Dừng chân tại Cổng trời Quản Bạ, ngắm Núi Đôi Cô Tiên.</li>
    <li><strong>17:00:</strong> Đến thị trấn Yên Minh, nhận phòng khách sạn.</li>
    <li><strong>19:00:</strong> Ăn tối và nghỉ đêm tại Yên Minh.</li>
</ul>'
WHERE day_number = 1
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04');

-- Ngày 2
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>07:00:</strong> Ăn sáng, khởi hành đi Cao nguyên đá Đồng Văn.</li>
    <li><strong>09:00:</strong> Thăm bản Sủng Là (Nhà của Pao), Dinh thự Vua Mèo.</li>
    <li><strong>11:00:</strong> Chinh phục Cột cờ Lũng Cú - điểm cực Bắc tổ quốc.</li>
    <li><strong>12:30:</strong> Ăn trưa tại Đồng Văn.</li>
    <li><strong>14:00:</strong> Chinh phục đèo Mã Pí Lèng. Đi thuyền trên sông Nho Quế, đi qua Hẻm Tu Sản.</li>
    <li><strong>18:00:</strong> Về thị trấn Đồng Văn, nhận phòng. Thưởng thức Cafe Phố Cổ.</li>
</ul>'
WHERE day_number = 2
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04');

-- Ngày 3
UPDATE itinerary_days
SET details = '<ul>
    <li><strong>07:00:</strong> Quý khách tham gia Chợ phiên Đồng Văn (nếu vào Chủ Nhật), mua sắm đặc sản vùng cao.</li>
    <li><strong>08:30:</strong> Khởi hành về lại Hà Nội.</li>
    <li><strong>12:00:</strong> Ăn trưa tại TP Hà Giang.</li>
    <li><strong>19:00:</strong> Về đến Hà Nội, chia tay đoàn.</li>
</ul>'
WHERE day_number = 3
AND tour_id = (SELECT tourid FROM tours WHERE tour_code = 'TOUR-HG-04');