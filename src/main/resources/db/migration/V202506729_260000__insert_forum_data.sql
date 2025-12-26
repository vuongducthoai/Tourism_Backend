-- 1. POST CATEGORIES (6 categories)
-- =============================================
INSERT INTO post_categories (category_name, slug, description, icon, color, post_count, display_order, is_active, created_at, updated_at) VALUES
('Kinh nghiệm du lịch', 'kinh-nghiem-du-lich', 'Chia sẻ những kinh nghiệm thực tế khi đi du lịch', '🎒', '#3B82F6', 0, 1, true, NOW(), NOW()),
('Hướng dẫn', 'huong-dan', 'Hướng dẫn chi tiết về các địa điểm, thủ tục', '📖', '#10B981', 0, 2, true, NOW(), NOW()),
('Review điểm đến', 'review-diem-den', 'Đánh giá chi tiết về các điểm đến du lịch', '⭐', '#F59E0B', 0, 3, true, NOW(), NOW()),
('Ẩm thực', 'am-thuc', 'Khám phá ẩm thực địa phương', '🍜', '#EF4444', 0, 4, true, NOW(), NOW()),
('Hỏi đáp', 'hoi-dap', 'Đặt câu hỏi và nhận câu trả lời từ cộng đồng', '❓', '#8B5CF6', 0, 5, true, NOW(), NOW()),
('Mẹo hay', 'meo-hay', 'Những mẹo nhỏ giúp chuyến đi thêm trọn vẹn', '💡', '#06B6D4', 0, 6, true, NOW(), NOW());

-- =============================================
-- 2. TAGS (15 tags)
-- =============================================
INSERT INTO tags (tag_name, slug, description, color, usage_count, is_active, created_at, updated_at) VALUES
('Du lịch bụi', 'du-lich-bui', 'Du lịch tiết kiệm, tự túc', '#3B82F6', 0, true, NOW(), NOW()),
('Gia đình', 'gia-dinh', 'Phù hợp cho gia đình có trẻ em', '#10B981', 0, true, NOW(), NOW()),
('Biển đảo', 'bien-dao', 'Các địa điểm biển, đảo', '#06B6D4', 0, true, NOW(), NOW()),
('Núi rừng', 'nui-rung', 'Leo núi, trekking', '#22C55E', 0, true, NOW(), NOW()),
('Phượt', 'phuot', 'Du lịch phượt bằng xe máy', '#F97316', 0, true, NOW(), NOW()),
('Văn hóa', 'van-hoa', 'Khám phá văn hóa địa phương', '#8B5CF6', 0, true, NOW(), NOW()),
('Chụp ảnh', 'chup-anh', 'Địa điểm đẹp để chụp ảnh', '#EC4899', 0, true, NOW(), NOW()),
('Tiết kiệm', 'tiet-kiem', 'Du lịch tiết kiệm chi phí', '#EAB308', 0, true, NOW(), NOW()),
('Sang trọng', 'sang-trong', 'Du lịch cao cấp', '#D946EF', 0, true, NOW(), NOW()),
('Mùa xuân', 'mua-xuan', 'Du lịch mùa xuân', '#10B981', 0, true, NOW(), NOW()),
('Mùa hè', 'mua-he', 'Du lịch mùa hè', '#F59E0B', 0, true, NOW(), NOW()),
('Mùa thu', 'mua-thu', 'Du lịch mùa thu', '#EF4444', 0, true, NOW(), NOW()),
('Mùa đông', 'mua-dong', 'Du lịch mùa đông', '#06B6D4', 0, true, NOW(), NOW()),
('Solo travel', 'solo-travel', 'Du lịch một mình', '#6366F1', 0, true, NOW(), NOW()),
('Honeymoon', 'honeymoon', 'Phù hợp cho tuần trăng mật', '#EC4899', 0, true, NOW(), NOW());

-- =============================================
-- 3. FORUM POSTS (10 posts)
-- Assuming user_id 1-5 exist
-- =============================================
INSERT INTO forum_posts (user_id, tour_id, category_id, title, content, summary, thumbnail_url, post_type,
    view_count, like_count, comment_count, bookmark_count, share_count, is_pinned, is_featured, status,
    published_at, created_at, updated_at) VALUES

-- Post 1: Kinh nghiệm đi Đà Nẵng
(1, NULL, 1, 'Kinh nghiệm du lịch Đà Nẵng 3 ngày 2 đêm chỉ với 3 triệu',
'# Kinh nghiệm du lịch Đà Nẵng tiết kiệm...',
'Chia sẻ chi tiết chi phí và kinh nghiệm du lịch Đà Nẵng 3 ngày chỉ 3 triệu đồng',
'https://picsum.photos/800/400?random=1',
'EXPERIENCE', 245, 28, 12, 15, 5, true, true, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 2: Hướng dẫn xin visa Thái Lan
(2, NULL, 2, 'Hướng dẫn chi tiết cách xin visa du lịch Thái Lan tự túc 2024',
'# Hướng dẫn xin visa Thái Lan...',
'Hướng dẫn từng bước xin visa du lịch Thái Lan tự túc, nhanh chóng, dễ dàng',
'https://picsum.photos/800/400?random=2',
'GUIDE', 189, 22, 8, 11, 3, false, true, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 3: Review Phú Quốc
(3, 1, 3, 'Review chi tiết chuyến đi Phú Quốc 4N3Đ - Thiên đường nghỉ dưỡng',
'# Review Phú Quốc - Hòn đảo ngọc...',
'Review chi tiết về chuyến đi Phú Quốc 4N3Đ với đầy đủ thông tin về chi phí, địa điểm',
'https://picsum.photos/800/400?random=3',
'REVIEW_SHARE', 312, 35, 15, 22, 7, false, true, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 4: Quán ăn ngon Hà Nội
(1, NULL, 4, 'Top 10 quán ăn nhất định phải thử khi đến Hà Nội',
'# Ẩm thực Hà Nội không thể bỏ qua...',
'Tổng hợp 10 quán ăn ngon, nổi tiếng nhất định phải thử khi đến Hà Nội',
'https://picsum.photos/800/400?random=4',
'BLOG', 167, 19, 9, 13, 4, false, false, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 5: Hỏi về Sapa mùa đông
(4, NULL, 5, 'Sapa mùa đông có lạnh không? Cần mang theo gì?',
'Mình dự định đi Sapa vào tháng 12, lần đầu đi nên hơi lo...',
'Cần tư vấn về thời tiết và chuẩn bị khi đi Sapa mùa đông tháng 12',
'https://picsum.photos/800/400?random=5',
'QA', 98, 8, 6, 4, 1, false, false, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 6: Mẹo chụp ảnh du lịch
(2, NULL, 6, '7 mẹo chụp ảnh du lịch đẹp như travel blogger',
'# Bí quyết chụp ảnh du lịch đẹp...',
'7 mẹo chụp ảnh du lịch đẹp mà ai cũng có thể áp dụng được ngay',
'https://picsum.photos/800/400?random=6',
'TIP', 234, 31, 11, 19, 6, false, false, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 7: Kinh nghiệm đi Hạ Long
(5, 2, 1, 'Du lịch Vịnh Hạ Long 2N1Đ: Kinh nghiệm và lịch trình chi tiết',
'# Vịnh Hạ Long - Di sản thế giới...',
'Kinh nghiệm và lịch trình chi tiết cho chuyến đi Vịnh Hạ Long 2 ngày 1 đêm',
'https://picsum.photos/800/400?random=7',
'EXPERIENCE', 276, 29, 14, 18, 5, false, true, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 8: Review khách sạn Nha Trang
(3, NULL, 3, 'Review Vinpearl Resort Nha Trang: Đáng đồng tiền bát gạo?',
'# Review Vinpearl Resort Nha Trang...',
'Review chi tiết về Vinpearl Resort Nha Trang từ vị trí, phòng ốc đến dịch vụ',
'https://picsum.photos/800/400?random=8',
'REVIEW_SHARE', 201, 24, 10, 16, 4, false, false, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 9: Món ăn vặt Sài Gòn
(1, NULL, 4, 'Ăn vặt Sài Gòn: 15 món nhất định phải thử',
'# Thiên đường ăn vặt Sài Gòn...',
'Tổng hợp 15 món ăn vặt nổi tiếng nhất định phải thử khi đến Sài Gòn',
'https://picsum.photos/800/400?random=9',
'BLOG', 156, 17, 7, 12, 3, false, false, 'PUBLISHED', NOW(), NOW(), NOW()),

-- Post 10: Chuẩn bị hành lý
(4, NULL, 6, 'Checklist chuẩn bị hành lý du lịch hoàn hảo',
'# Checklist hành lý du lịch...',
'Checklist chi tiết giúp bạn không bỏ sót bất kỳ thứ gì khi đi du lịch',
'https://picsum.photos/800/400?random=10',
'TIP', 189, 21, 8, 14, 4, true, false, 'PUBLISHED', NOW(), NOW(), NOW());

-- =============================================
-- 4. POST TAGS (Link posts with tags)
-- =============================================
INSERT INTO post_tags (post_id, tag_id, created_at) VALUES
(1, 1, NOW()), (1, 8, NOW()), (1, 3, NOW()), (1, 14, NOW()), -- Post 1
(2, 6, NOW()), -- Post 2
(3, 3, NOW()), (3, 2, NOW()), (3, 9, NOW()), (3, 15, NOW()), -- Post 3
(4, 6, NOW()), (4, 8, NOW()), -- Post 4
(5, 4, NOW()), (5, 13, NOW()), -- Post 5
(6, 7, NOW()), -- Post 6
(7, 3, NOW()), (7, 2, NOW()), (7, 6, NOW()), -- Post 7
(8, 3, NOW()), (8, 9, NOW()), (8, 15, NOW()), -- Post 8
(9, 6, NOW()), (9, 8, NOW()), -- Post 9
(10, 1, NOW()); -- Post 10

-- =============================================
-- 5. POST IMAGES (Sample images for posts)
-- =============================================
INSERT INTO post_images (post_id, image_url, caption, alt_text, display_order, file_size, mime_type, created_at, updated_at) VALUES
(1, 'https://picsum.photos/800/600?random=11', 'Bãi biển Mỹ Khê tuyệt đẹp', 'Biển Mỹ Khê', 1, 245000, 'image/jpeg', NOW(), NOW()),
(1, 'https://picsum.photos/800/600?random=12', 'Cầu Rồng về đêm', 'Cầu Rồng', 2, 189000, 'image/jpeg', NOW(), NOW()),
(3, 'https://picsum.photos/800/600?random=14', 'Bãi Sao Phú Quốc', 'Bãi Sao', 1, 298000, 'image/jpeg', NOW(), NOW()),
(6, 'https://picsum.photos/800/600?random=16', 'Pose chụp ảnh', 'Chụp ảnh', 1, 215000, 'image/jpeg', NOW(), NOW());

-- =============================================
-- 6. POST COMMENTS (Hierarchical comments)
-- =============================================
INSERT INTO post_comments (post_id, user_id, parent_comment_id, content, like_count, is_edited, status, created_at, updated_at) VALUES
(1, 2, NULL, 'Bài viết rất hữu ích, cảm ơn bạn đã chia sẻ!', 5, false, 'PUBLISHED', NOW(), NOW()),
(1, 1, 1, 'Cảm ơn bạn, hy vọng bạn sẽ có chuyến đi vui vẻ!', 2, false, 'PUBLISHED', NOW(), NOW()),
(3, 4, NULL, 'Phú Quốc mùa này có mưa không bạn?', 3, false, 'PUBLISHED', NOW(), NOW()),
(5, 2, NULL, 'Mùa đông Sapa lạnh lắm, nhớ mang áo ấm dày nhé!', 10, false, 'PUBLISHED', NOW(), NOW());

-- =============================================
-- 7. POST LIKES & BOOKMARKS
-- =============================================
INSERT INTO post_likes (post_id, user_id, created_at) VALUES
(1, 2, NOW()), (1, 3, NOW()), (1, 4, NOW()), (3, 1, NOW()), (3, 5, NOW());

INSERT INTO post_bookmarks (post_id, user_id, folder_name, note, created_at) VALUES
(1, 2, 'Kế hoạch du lịch', 'Tham khảo lịch trình Đà Nẵng', NOW()),
(3, 1, 'Địa điểm mơ ước', 'Resort đẹp ở Phú Quốc', NOW());

-- =============================================
-- 8. FOLLOWERS
-- =============================================
INSERT INTO followers (following_id, follower_user_id, followed_at) VALUES
(1, 2, NOW()), (1, 3, NOW()), (2, 1, NOW()), (3, 4, NOW()), (5, 1, NOW());

-- =============================================
-- 9. USER STATISTICS (Update stats)
-- =============================================
INSERT INTO user_statistics (customer_id, total_posts, total_comments, total_likes_received, total_views_received, total_followers, total_following, reputation_points, last_post_at, last_active_at, created_at, updated_at) VALUES
(1, 3, 1, 45, 500, 2, 1, 150, NOW(), NOW(), NOW(), NOW()),
(2, 2, 2, 53, 423, 1, 1, 120, NOW(), NOW(), NOW(), NOW()),
(3, 2, 0, 55, 513, 0, 1, 110, NOW(), NOW(), NOW(), NOW()),
(4, 2, 1, 29, 287, 0, 1, 80, NOW(), NOW(), NOW(), NOW()),
(5, 1, 0, 29, 276, 1, 0, 90, NOW(), NOW(), NOW(), NOW());