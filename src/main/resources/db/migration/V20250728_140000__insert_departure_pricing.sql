-- Giá phụ thu phòng đơn cho ngày 29/11/2025 (Ví dụ ID departure là 100)
INSERT INTO departure_pricing (
    passenger_type, age_description, sale_price, original_price, departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'SINGLE_SUPPLEMENT',
    'Phụ thu phòng đơn',
    1900000,
    1900000,
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);