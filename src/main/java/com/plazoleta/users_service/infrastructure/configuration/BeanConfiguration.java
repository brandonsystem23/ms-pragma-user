package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.api.AuthServicePort;
import com.plazoleta.users_service.domain.api.CreateOwnerUseCase;
import com.plazoleta.users_service.domain.spi.JwtProviderPort;
import com.plazoleta.users_service.domain.spi.PasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.UsuarioPersistencePort;
import com.plazoleta.users_service.domain.usecase.CreateOwnerUseCaseImpl;
import com.plazoleta.users_service.domain.usecase.LoginUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public AuthServicePort authServicePort(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtProviderPort jwtProviderPort
    ) {

        return new LoginUseCase(
                usuarioPersistencePort,
                passwordEncoderPort,
                jwtProviderPort
        );

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CreateOwnerUseCase createOwnerUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort) {

        return new CreateOwnerUseCaseImpl(
                usuarioPersistencePort,
                passwordEncoderPort
        );
    }

}
