package com.khanh.timekeeping.constants;


import jakarta.persistence.AttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Gender {

    FEMALE(0, "Nữ"),
    MALE(1, "Nam");

    private final Integer value;
    private final String label;

    public static Gender of(Integer value) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giá trị Gender tương ứng với " + value));
    }

    public static class EnumConverter implements AttributeConverter<Gender, Integer> {

        @Override
        public Integer convertToDatabaseColumn(Gender gender) {
            return gender.getValue();
        }

        @Override
        public Gender convertToEntityAttribute(Integer gender) {
            return Gender.of(gender);
        }

    }

}

