package com.ekino.team2.punctuality.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Document(collection = "users1")
public class User {
    @Id
    private ObjectId id;
    @Field(value = "name")
    private String name;
    @Field(value = "deviceUserId")
    private String deviceUserId;

    @Field(value = "role")
    private String role;

    @Field(value = "createdAt")
    LocalDateTime createdAt;
    @Field(value = "updatedAt")
    LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(deviceUserId, user.deviceUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, deviceUserId);
    }
}
