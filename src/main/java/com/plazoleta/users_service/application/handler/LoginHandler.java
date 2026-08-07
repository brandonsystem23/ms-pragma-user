package com.plazoleta.users_service.application.handler;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import reactor.core.publisher.Mono;

public interface LoginHandler {

    Mono<LoginResponse> login(LoginRequest request);

}
