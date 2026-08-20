package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.in.RetrieveUserUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.RestaurantEmployeePersistencePort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.service.AssignEmployeeToRestaurantService;
import com.plazoleta.users_service.domain.service.LoginService;
import com.plazoleta.users_service.domain.service.LogoutService;
import com.plazoleta.users_service.domain.service.RegisterUserService;
import com.plazoleta.users_service.domain.service.RetrieveUserService;
import com.plazoleta.users_service.domain.service.validation.DomainLoginValidator;
import com.plazoleta.users_service.domain.service.validation.DomainUserValidator;
import com.plazoleta.users_service.domain.service.validation.UserRegistrationValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

@Configuration
public class BeanConfiguration {

    @Bean
    public DomainUserValidator domainUserValidator() {
        return new DomainUserValidator();
    }

    @Bean
    public DomainLoginValidator domainLoginValidator() {
        return new DomainLoginValidator();
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            AuthSessionPort authSessionPort,
            DomainLoginValidator domainLoginValidator
    ) {
        return new LoginService(
                userPersistencePort,
                passwordEncoderPort,
                authSessionPort,
                domainLoginValidator
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
            UserRegistrationValidator userRegistrationValidator,
            DomainUserValidator domainUserValidator,
            AssignEmployeeToRestaurantService assignEmployeeToRestaurantService
    ) {
        return new RegisterUserService(
                userPersistencePort,
                passwordEncoderPort,
                userRegistrationValidator,
                domainUserValidator,
                assignEmployeeToRestaurantService
        );
    }

    @Bean
    public RetrieveUserUseCase retrieveUserUseCase(
            UserPersistencePort userPersistencePort
    ) {
        return new RetrieveUserService(
                userPersistencePort
        );
    }

    @Bean
    public AssignEmployeeToRestaurantService assignEmployeeToRestaurantService(
            RestaurantEmployeePersistencePort restaurantEmployeePersistencePort
    ) {
        return new AssignEmployeeToRestaurantService(restaurantEmployeePersistencePort);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Duration authTokenExpiration(@Value("${auth.token.expiration}") Long expirationMinutes) {
        return Duration.ofMinutes(expirationMinutes);
    }
}
