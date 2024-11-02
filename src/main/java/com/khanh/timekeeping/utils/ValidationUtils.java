package com.khanh.timekeeping.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
@RequiredArgsConstructor
public class ValidationUtils {
    public boolean isValidEmail(String email) {
        // Regular expression pattern for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);
        // Match the input email with the pattern
        return email != null && pattern.matcher(email).matches();
    }

}
