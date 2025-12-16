ALTER TABLE coupons
ADD COLUMN coupon_type VARCHAR(20) NOT NULL DEFAULT 'GLOBAL';

UPDATE coupons
SET coupon_type = CASE
    WHEN departure_id IS NOT NULL THEN 'DEPARTURE'
    ELSE 'GLOBAL'
END;

CREATE INDEX idx_coupons_type ON coupons(coupon_type);
CREATE INDEX idx_coupons_departure ON coupons(departure_id) WHERE departure_id IS NOT NULL;