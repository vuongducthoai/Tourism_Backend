-- Giả sử departure_id của ngày 12/12/2025 là (SELECT departureid ...)

-- 1. CHIỀU ĐI (SGN -> HAN)
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'OUTBOUND', 'VN206', 'Vietnam Airlines',
    'SGN', 'HAN',
    '2025-12-12 06:00:00', '2025-12-12 08:05:00',
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-09-02' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

-- 2. CHIỀU VỀ (HAN -> SGN)
INSERT INTO departure_transports (
    type, transport_code, vehicle_name,
    start_point, end_point,
    depart_time, arrival_time,
    departure_id,
    created_at, updated_at, created_by, updated_by, is_deleted
) VALUES (
    'INBOUND', 'VN265', 'Vietnam Airlines',
    'HAN', 'SGN',
    '2025-12-17 20:30:00', '2025-12-17 22:40:00',
    (SELECT departureid FROM tour_departures WHERE departure_date = '2025-11-20' LIMIT 1),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', false
);

