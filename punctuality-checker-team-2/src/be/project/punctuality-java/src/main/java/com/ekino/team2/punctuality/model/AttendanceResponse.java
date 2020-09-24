package com.ekino.team2.punctuality.model;

import com.ekino.team2.punctuality.entity.Attendance;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AttendanceResponse {
    private int total;
    private List<Attendance> attendances;
}
