package com.tourism.backend.enums;

public enum Region {
    NORTH("Miền Bắc"),
    CENTRAL("Miền Trung"),
    SOUTH("Miền Nam");

    private final String label;
    Region(String label) { this.label = label; }
}
