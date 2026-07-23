package com.musiclv.booking;

public enum BookingStatus {

    BOOKED("예매완료"),
    USED("관람완료"),
    CANCELLED("예매취소");

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isCancellable() {
        return this == BOOKED;
    }
}
