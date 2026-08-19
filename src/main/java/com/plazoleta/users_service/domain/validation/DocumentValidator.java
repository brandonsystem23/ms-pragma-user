package com.plazoleta.users_service.domain.validation;

import java.util.regex.Pattern;

public final class DocumentValidator {

    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("\\d+");

    private DocumentValidator() {
    }

    public static boolean isValid(String numberDocument) {
        return numberDocument != null && DOCUMENT_PATTERN.matcher(numberDocument).matches();
    }
}