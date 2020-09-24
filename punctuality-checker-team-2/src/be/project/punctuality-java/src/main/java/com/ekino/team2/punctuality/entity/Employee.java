package com.ekino.team2.punctuality.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@Document(collection = "Employee")
public class Employee {
    @Id
    private Long id;
    @Field(value = "users")
    private User user;
    @Field(value = "isActive")
    private boolean isActive;
    @Field(value = "lastWorkingDate")
    private LocalDate lastWorkingDate;
    @Field(value = "createdAt")
    LocalDateTime createdAt;
    @Field(value = "updatedAt")
    LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(user, employee.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
