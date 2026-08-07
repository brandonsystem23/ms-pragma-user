package com.plazoleta.users_service.domain.spi;

public interface PasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword,
                    String encodedPassword);

}