package com.plazoleta.users_service.domain.service.validation;

import java.util.regex.Pattern;

public final class PhoneValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d+$");
    private static final int MAX_LENGTH = 13;

    private PhoneValidator() {
    }

    public static boolean hasValidFormat(String phone) {
        return phone != null && phone.length() <= MAX_LENGTH
                && PHONE_PATTERN.matcher(phone).matches();
    }
}
