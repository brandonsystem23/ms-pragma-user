package com.plazoleta.users_service.domain.spi;

import com.plazoleta.users_service.domain.model.Usuario;

public interface JwtProviderPort {

    String generateToken(Usuario usuario);

}
