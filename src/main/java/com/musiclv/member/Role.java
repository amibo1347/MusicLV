package com.musiclv.member;

public enum Role {

    USER("일반회원"),
    ADMIN("관리자");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Spring Security 가 기대하는 권한 문자열 */
    public String authority() {
        return "ROLE_" + name();
    }
}
