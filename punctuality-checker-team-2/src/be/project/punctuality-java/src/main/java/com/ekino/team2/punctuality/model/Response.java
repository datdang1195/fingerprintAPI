package com.ekino.team2.punctuality.model;

import com.ekino.team2.punctuality.entity.Employee;
import com.ekino.team2.punctuality.entity.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Response {
    private int total;
    private List<UserStatus> statusList;
    private List<RangeStatus> rangeStatusList;
    private List<Employee> employees;
    private String message;
}
