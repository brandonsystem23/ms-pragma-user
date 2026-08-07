package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import com.plazoleta.users_service.domain.service.LoginService;
import com.plazoleta.users_service.domain.service.RegisterUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public LoginUseCase loginUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            AuthSessionPort authSessionPort
    ) {
        return new LoginService(
                usuarioPersistencePort,
                passwordEncoderPort,
                authSessionPort
        );
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        return new RegisterUserService(
                usuarioPersistencePort,
                passwordEncoderPort
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
