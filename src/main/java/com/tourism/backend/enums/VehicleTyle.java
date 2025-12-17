package com.tourism.backend.enums;

public enum VehicleTyle {
    FLIGHT("Máy bay"),
    BUS("Xe khách"),
    TRAIN("Tàu hỏa"),
    SHIP("Tàu thủy"),
    CAR("Xe ô tô");

    private final String displayName;

    VehicleTyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
