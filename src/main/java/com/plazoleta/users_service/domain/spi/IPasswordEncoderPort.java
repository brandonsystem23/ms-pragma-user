package com.plazoleta.users_service.domain.spi;

public interface IPasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}