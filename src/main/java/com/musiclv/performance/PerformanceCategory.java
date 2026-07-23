package com.musiclv.performance;

public enum PerformanceCategory {

    MUSICAL("뮤지컬"),
    CONCERT("콘서트"),
    CLASSIC("클래식/무용");

    private final String label;

    PerformanceCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
