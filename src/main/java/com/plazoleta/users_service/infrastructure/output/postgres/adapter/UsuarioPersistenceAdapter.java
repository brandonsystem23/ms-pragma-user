package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.spi.UsuarioPersistencePort;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RolEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.UsuarioEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.mapper.UsuarioEntityMapper;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RolRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioEntityMapper usuarioEntityMapper;

    @Override
    public Mono<Usuario> findByDocumento(String documento) {

        return usuarioRepository
                .findByDocumentoIdentidad(documento)
                .flatMap(usuarioEntity ->
                        rolRepository
                                .findById(usuarioEntity.getRolId())
                                .map(rolEntity ->
                                        usuarioEntityMapper.toDomain(
                                                usuarioEntity,
                                                rolEntity
                                        )
                                )
                );
    }

    @Override
    public Mono<Boolean> existsByCorreo(String correo) {

        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public Mono<Boolean> existsByDocumento(String documento) {

        return usuarioRepository.existsByDocumentoIdentidad(documento);
    }

    @Override
    public Mono<Rol> findRoleByNombre(String nombre) {

        return rolRepository
                .findByNombre(nombre)
                .map(this::mapRolToDomain);
    }

    @Override
    public Mono<Usuario> save(Usuario usuario) {

        UsuarioEntity usuarioEntity =
                usuarioEntityMapper.toEntity(usuario);

        return usuarioRepository
                .save(usuarioEntity)
                .flatMap(savedEntity ->
                        rolRepository
                                .findById(savedEntity.getRolId())
                                .map(rolEntity ->
                                        usuarioEntityMapper.toDomain(
                                                savedEntity,
                                                rolEntity
                                        )
                                )
                );
    }

    private Rol mapRolToDomain(RolEntity rolEntity) {

        return Rol.builder()
                .id(rolEntity.getId())
                .nombre(rolEntity.getNombre())
                .descripcion(rolEntity.getDescripcion())
                .build();
    }

}
