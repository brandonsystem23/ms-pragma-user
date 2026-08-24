package com.plazoleta.users_service.domain.builder;

import com.plazoleta.users_service.domain.model.RestaurantEmployee;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;

public final class UserBuilder {

    private UserBuilder() {

    }

    public static User buildUser(RegisterUserCommand command, String normalizedEmail, Role role, String password) {
        return User.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .numberDocument(command.numberDocument())
                .phone(command.phone())
                .birthDate(command.birthDate())
                .email(normalizedEmail)
                .password(password)
                .status(true)
                .role(role)
                .build();
    }

    public static RestaurantEmployee buildRestaurantEmployee(Long restaurantId, Long employeeId) {
        return  RestaurantEmployee.builder()
                .employeeId(employeeId)
                .restaurantId(restaurantId)
                .build();
    }


}
