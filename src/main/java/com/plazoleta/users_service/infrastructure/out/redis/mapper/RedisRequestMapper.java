package com.plazoleta.users_service.infrastructure.out.redis.mapper;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.infrastructure.out.redis.dto.AuthSessionRedisValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisRequestMapper {

    AuthSession toDomain(AuthSessionRedisValue authSessionRedisValue);

    AuthSessionRedisValue toInsert(AuthSession authSession);

}
