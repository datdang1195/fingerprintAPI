package com.ekino.team2.punctuality.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "CustomSequence")
public class CustomSequence {
    @Id
    private String id;
    private int seq;
}