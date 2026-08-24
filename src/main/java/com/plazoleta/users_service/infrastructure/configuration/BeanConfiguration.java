package com.plazoleta.users_service.infrastructure.configuration;

import com.plazoleta.users_service.domain.api.IAuthServicePort;
import com.plazoleta.users_service.domain.api.IUserRegisterServicePort;
import com.plazoleta.users_service.domain.api.IUserRetrieveServicePort;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.validation.user.AssignerRestaurantValidator;
import com.plazoleta.users_service.domain.usecase.AuthUseCase;
import com.plazoleta.users_service.domain.usecase.RegisterUserUseCase;
import com.plazoleta.users_service.domain.usecase.RetrieveUserUseCase;
import com.plazoleta.users_service.domain.validation.user.DomainLoginValidator;
import com.plazoleta.users_service.domain.validation.user.DomainUserValidator;
import com.plazoleta.users_service.domain.validation.user.LoginValidator;
import com.plazoleta.users_service.domain.validation.user.UserRegistrationValidator;
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
            IAuthCachePort iAuthCachePort,
            DomainLoginValidator domainLoginValidator,
            LoginValidator loginValidator
    ) {
        return new AuthUseCase(
                iAuthCachePort,
                domainLoginValidator,
                loginValidator
        );
    }

    @Bean
    public UserRegistrationValidator userRegistrationValidator(
            IUserPersistencePort iUserPersistencePort
    ) {
        return new UserRegistrationValidator(iUserPersistencePort);
    }

    @Bean
    public LoginValidator loginValidator(
            IUserPersistencePort iUserPersistencePort,
            IPasswordEncoderPort iPasswordEncoderPort
    ) {
        return new LoginValidator(iUserPersistencePort,
                iPasswordEncoderPort);
    }

    @Bean
    public IUserRegisterServicePort registerUserUseCase(
            IUserPersistencePort iUserPersistencePort,
            IPasswordEncoderPort iPasswordEncoderPort,
            UserRegistrationValidator userRegistrationValidator,
            DomainUserValidator domainUserValidator,
            AssignerRestaurantValidator assignEmployeeService
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
    public AssignerRestaurantValidator assignEmployeeToRestaurantService(
            IRestaurantEmployeePersistencePort iRestaurantEmployeePersistencePort
    ) {
        return new AssignerRestaurantValidator(iRestaurantEmployeePersistencePort);
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
