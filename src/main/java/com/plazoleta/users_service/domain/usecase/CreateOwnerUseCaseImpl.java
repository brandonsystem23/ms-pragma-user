package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.api.CreateOwnerUseCase;
import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.spi.PasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.UsuarioPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateOwnerUseCaseImpl implements CreateOwnerUseCase {

    private static final String ROLE_OWNER = "PROPIETARIO";

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public Mono<Usuario> createOwner(Usuario usuario) {

        return validateDocument(usuario.getDocumentoIdentidad())
                .then(validateEmail(usuario.getCorreo()))
                .then(usuarioPersistencePort.findRoleByNombre(ROLE_OWNER))
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "El rol PROPIETARIO no existe"
                                )
                        )
                )
                .flatMap(rol -> {

                    Usuario owner = Usuario.builder()
                            .id(usuario.getId())
                            .nombre(usuario.getNombre())
                            .apellido(usuario.getApellido())
                            .documentoIdentidad(
                                    usuario.getDocumentoIdentidad()
                            )
                            .telefono(usuario.getTelefono())
                            .fechaNacimiento(
                                    usuario.getFechaNacimiento()
                            )
                            .correo(usuario.getCorreo())
                            .password(
                                    passwordEncoderPort.encode(
                                            usuario.getPassword()
                                    )
                            )
                            .activo(true)
                            .rol(rol)
                            .build();

                    return usuarioPersistencePort.save(owner);
                });
    }

    private Mono<Void> validateDocument(String documento) {

        return usuarioPersistencePort
                .existsByDocumento(documento)
                .flatMap(exists -> {

                    if (exists) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "El documento de identidad ya está registrado"
                                )
                        );
                    }

                    return Mono.empty();
                });
    }

    private Mono<Void> validateEmail(String correo) {

        return usuarioPersistencePort
                .existsByCorreo(correo)
                .flatMap(exists -> {

                    if (exists) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "El correo ya está registrado"
                                )
                        );

                    }

                    return Mono.empty();
                });
    }
}
