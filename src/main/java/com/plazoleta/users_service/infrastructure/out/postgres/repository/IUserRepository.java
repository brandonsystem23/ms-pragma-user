package com.plazoleta.users_service.infrastructure.out.postgres.repository;

import com.plazoleta.users_service.infrastructure.out.postgres.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByEmailAndStatusTrue(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNumberDocument(String numberDocument);

    Mono<UserEntity> findByIdAndStatusTrue(Long id);

    @Query("""
            SELECT r.id
            FROM users u
            INNER JOIN restaurant r ON r.owner_id = u.id
            WHERE u.id = :ownerId
              AND u.status = true
              AND r.status = true
            LIMIT 1
            """)
    Mono<Long> findRestaurantIdByOwnerId(Long ownerId);
}
