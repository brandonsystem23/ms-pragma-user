package com.plazoleta.users_service.domain.model;

import lombok.Builder;

@Builder
public record RestaurantEmployee (

        Long restaurantId,

        Long employeeId
) {
}
