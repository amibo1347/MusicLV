package com.musiclv.product;

public enum Category {

    GUITAR("기타"),
    BASS("베이스"),
    DRUM("드럼"),
    KEYBOARD("건반"),
    WIND("관악기"),
    STRINGS("현악기"),
    AUDIO("음향장비"),
    ACCESSORY("액세서리");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
