package com.ekino.team2.punctuality.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@Document(collection = "RangeStatus")
public class RangeStatus {
    @Id
    private Long id;

    private String userCode;
    private String userName;
    private float workingDay;
    private float offDay;
    private float lateSession;
    private float absentSession;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int totalWorkingDate;
}
