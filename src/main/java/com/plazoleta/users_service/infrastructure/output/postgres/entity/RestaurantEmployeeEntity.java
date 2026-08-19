package com.plazoleta.users_service.infrastructure.output.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("restaurant_employee")
public class RestaurantEmployeeEntity {

    @Id
    private Long id;

    @Column("restaurant_id")
    private Long restaurantId;

    @Column("employee_id")
    private Long employeeId;
}
