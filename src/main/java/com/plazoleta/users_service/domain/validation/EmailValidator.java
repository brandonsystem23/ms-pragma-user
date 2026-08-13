package com.plazoleta.users_service.domain.validation;

import com.plazoleta.users_service.domain.util.EmailNormalizer;

import java.util.regex.Pattern;

public final class EmailValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private EmailValidator() {
    }

    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        String normalizedEmail = EmailNormalizer.normalize(email);

        return !normalizedEmail.isEmpty()
                && EMAIL_PATTERN.matcher(normalizedEmail).matches();
    }

    public static boolean isInvalid(String email) {
        return !isValid(email);
    }

}