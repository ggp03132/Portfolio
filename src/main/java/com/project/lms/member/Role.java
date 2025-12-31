package com.project.lms.member;

public enum Role {
    ADMIN(0, "관리자"),
    USER(1, "일반인"),
    INSTRUCTOR(2, "강사");

    private final int value;
    private final String description;

    Role(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    // 숫자로 enum 찾기
    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}
