package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.service.LoginService;
import com.plazoleta.users_service.domain.service.LogoutService;
import com.plazoleta.users_service.domain.service.RegisterUserService;
import com.plazoleta.users_service.domain.service.UserRegistrationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public LoginUseCase loginUseCase(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            AuthSessionPort authSessionPort
    ) {
        return new LoginService(
                userPersistencePort,
                passwordEncoderPort,
                authSessionPort
        );
    }

    @Bean
    public LogoutUseCase logoutUseCase(AuthSessionPort authSessionPort) {
        return new LogoutService(authSessionPort);
    }

    @Bean
    public UserRegistrationValidator userRegistrationValidator(
            UserPersistencePort userPersistencePort
    ) {
        return new UserRegistrationValidator(userPersistencePort);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            UserRegistrationValidator userRegistrationValidator
    ) {
        return new RegisterUserService(
                userPersistencePort,
                passwordEncoderPort,
                userRegistrationValidator
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
