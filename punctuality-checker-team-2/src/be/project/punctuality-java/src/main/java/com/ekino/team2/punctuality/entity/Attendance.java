package com.ekino.team2.punctuality.entity;

import com.ekino.team2.punctuality.model.Machine;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "attendances1")
public class Attendance {

    @Id
    private ObjectId id;
    @Field(value = "deviceUserId")
    String deviceUserId;
    @Field(value = "recordTime")
    LocalDateTime recordTime;
    @Field(value = "ip")
    String ip;
    @Field(value = "machine")
    Machine machine;
    @Field(value = "user")
    User user;
    @Field(value = "createdAt")
    LocalDate createdAt;
    @Field(value = "updatedAt")
    LocalDate updatedAt;
}
