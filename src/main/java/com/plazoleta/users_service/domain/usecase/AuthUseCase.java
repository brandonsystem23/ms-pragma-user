package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.builder.AuthBuilder;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.api.IAuthServicePort;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.validation.user.DomainLoginValidator;
import com.plazoleta.users_service.domain.validation.EmailNormalizer;
import com.plazoleta.users_service.domain.validation.user.LoginValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IAuthCachePort iAuthCachePort;
    private final DomainLoginValidator domainLoginValidator;
    private final LoginValidator loginValidator;

    @Override
    public Mono<AuthResult> login(LoginCommand loginCommand) {
        return Mono.defer(() -> {

            String normalizedEmail = EmailNormalizer.normalize(loginCommand.email());

            domainLoginValidator.validate(loginCommand);

            return loginValidator.validate(loginCommand, normalizedEmail)
                    .flatMap(user -> iAuthCachePort.createSession(
                                    AuthBuilder.buildAuthSession(user)
                            )
                            .map(token -> AuthBuilder.buildAuthResult(user, token)));
        });
    }

    @Override
    public Mono<Void> logout(String token) {
        return iAuthCachePort.deleteByToken(token);
    }
}
