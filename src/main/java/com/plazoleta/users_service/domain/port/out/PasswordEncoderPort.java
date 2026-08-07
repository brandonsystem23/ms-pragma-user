package com.plazoleta.users_service.domain.port.out;

public interface PasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}