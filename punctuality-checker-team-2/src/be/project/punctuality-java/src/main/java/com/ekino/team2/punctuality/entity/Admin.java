package com.ekino.team2.punctuality.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "Admin")
public class Admin {
    @Id
    private Long id;
    @Field(value = "username")
    private String fullName;
    @Field(value = "role")
    private String role;
    @Field(value = "googleAccount")
    private String googleAccount;
}
