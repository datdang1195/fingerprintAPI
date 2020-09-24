package com.ekino.team2.punctuality.model;

import com.ekino.team2.punctuality.entity.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Request {
    private String fromDate;
    private String toDate;
    private boolean range;
    private String email;
    private List<Employee> employees;
}
