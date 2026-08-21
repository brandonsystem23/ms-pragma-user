package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.api.IAuthServicePort;
import com.plazoleta.users_service.domain.api.IUserRegisterServicePort;
import com.plazoleta.users_service.domain.api.IUserRetrieveServicePort;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.service.AssignEmployeeService;
import com.plazoleta.users_service.domain.usecase.AuthUseCase;
import com.plazoleta.users_service.domain.usecase.RegisterUserUseCase;
import com.plazoleta.users_service.domain.usecase.RetrieveUserUseCase;
import com.plazoleta.users_service.domain.validation.DomainLoginValidator;
import com.plazoleta.users_service.domain.validation.DomainUserValidator;
import com.plazoleta.users_service.domain.validation.UserRegistrationValidator;
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
    public IAuthServicePort loginUseCase(
            IUserPersistencePort iUserPersistencePort,
            IPasswordEncoderPort iPasswordEncoderPort,
            IAuthCachePort iAuthCachePort,
            DomainLoginValidator domainLoginValidator
    ) {
        return new AuthUseCase(
                iUserPersistencePort,
                iPasswordEncoderPort,
                iAuthCachePort,
                domainLoginValidator
        );
    }

    @Bean
    public UserRegistrationValidator userRegistrationValidator(
            IUserPersistencePort iUserPersistencePort
    ) {
        return new UserRegistrationValidator(iUserPersistencePort);
    }

    @Bean
    public IUserRegisterServicePort registerUserUseCase(
            IUserPersistencePort iUserPersistencePort,
            IPasswordEncoderPort iPasswordEncoderPort,
            UserRegistrationValidator userRegistrationValidator,
            DomainUserValidator domainUserValidator,
            AssignEmployeeService assignEmployeeService
    ) {
        return new RegisterUserUseCase(
                iUserPersistencePort,
                iPasswordEncoderPort,
                userRegistrationValidator,
                domainUserValidator,
                assignEmployeeService
        );
    }

    @Bean
    public IUserRetrieveServicePort retrieveUserUseCase(
            IUserPersistencePort iUserPersistencePort
    ) {
        return new RetrieveUserUseCase(
                iUserPersistencePort
        );
    }

    @Bean
    public AssignEmployeeService assignEmployeeToRestaurantService(
            IRestaurantEmployeePersistencePort iRestaurantEmployeePersistencePort
    ) {
        return new AssignEmployeeService(iRestaurantEmployeePersistencePort);
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
