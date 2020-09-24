package com.ekino.team2.punctuality.service;

import com.ekino.team2.punctuality.entity.Attendance;
import com.ekino.team2.punctuality.exception.Constant;
import com.ekino.team2.punctuality.exception.MyException;
import com.ekino.team2.punctuality.model.Request;
import com.ekino.team2.punctuality.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    AttendanceRepository attendanceSchemaRepository;

    public List<Attendance> getAttendance(Request attendanceRequest) {
        List<Attendance> attendances;
        try {
            LocalDateTime fromDate = null;
            LocalDateTime toDate = null;
            boolean isRange = attendanceRequest.isRange();

            String sFromDate = attendanceRequest.getFromDate();
            String sToDate = attendanceRequest.getToDate();
            if (!isRange) {
                if (StringUtils.hasText(sFromDate)) {
                    fromDate = stringToDate(sFromDate).atTime(05, 59);
                    toDate = fromDate.withHour(17).withMinute(01);
                }
            } else {
                if (StringUtils.hasText(sFromDate))
                    fromDate = stringToDate(sFromDate).atTime(05, 59);
                if (StringUtils.hasText(sToDate))
                    toDate = stringToDate(sToDate).atTime(17, 01);
            }

            if (fromDate == null && toDate == null)
                throw new MyException(Constant.TIME_NULL, Constant.TIME_NULL_MSG, "");
            if (fromDate != null && toDate == null) {
                toDate = fromDate.plusMonths(1);
            } else if (fromDate == null && toDate != null)
                fromDate = toDate.minusMonths(1);
            attendances = attendanceSchemaRepository.findAttendanceByRecordTimeBetweenOrderByDeviceUserId(fromDate, toDate);
        } catch (MyException e) {
            throw e;
        } catch (Exception e) {
            throw new MyException(Constant.GET_ATTENDANCE_ERROR, "", e.getMessage());
        }
        return attendances;
    }


    public LocalDate stringToDate(String date) {
        try {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            throw new MyException(Constant.PARSE_TIME_ERROR, Constant.PARSE_TIME_ERROR_MSG + date, e.getMessage());
        }
    }
}
