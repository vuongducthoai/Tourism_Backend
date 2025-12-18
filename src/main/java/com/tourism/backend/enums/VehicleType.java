package com.tourism.backend.enums;

public enum VehicleType {
    PLANE("Máy bay"),
    BUS("Xe khách"),
    TRAIN("Tàu hỏa"),
    SHIP("Tàu thủy"),
    CAR("Xe ô tô");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
