package com.khanh.timekeeping.entities.enums;

import java.util.Arrays;
import java.util.Objects;

public enum UserStatus {
    UNKNOWN,
    INACTIVE,
    ACTIVE,
    LOCK;

    public static UserStatus of(Integer value) {
        if (Objects.isNull(value)) return UNKNOWN;
        return Arrays.stream(values())
                .filter(userStatus -> userStatus.ordinal() == value)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
