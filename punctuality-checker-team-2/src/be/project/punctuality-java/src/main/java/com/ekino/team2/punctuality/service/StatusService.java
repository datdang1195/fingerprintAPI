package com.ekino.team2.punctuality.service;

import com.ekino.team2.punctuality.entity.Attendance;
import com.ekino.team2.punctuality.entity.Employee;
import com.ekino.team2.punctuality.entity.User;
import com.ekino.team2.punctuality.entity.UserStatus;
import com.ekino.team2.punctuality.exception.Constant;
import com.ekino.team2.punctuality.exception.MyException;
import com.ekino.team2.punctuality.model.RangeStatus;
import com.ekino.team2.punctuality.model.StatusInfo;
import com.ekino.team2.punctuality.repository.EmployeeRepository;
import com.ekino.team2.punctuality.repository.RangeStatusRepository;
import com.ekino.team2.punctuality.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatusService {
    @Autowired
    AttendanceService attendanceService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    @Autowired
    UserStatusRepository userStatusRepository;

    @Autowired
    RangeStatusRepository rangeStatusRepository;

    public List<UserStatus> createDailyStatus(List<Attendance> allAttendance) {
        List<UserStatus> userStatusBefore = userStatusRepository.findByRecordDate(allAttendance.get(0).getRecordTime().toLocalDate());
        //delete all userStatus this date before import
        if (!userStatusBefore.isEmpty())
            userStatusRepository.deleteAll(userStatusBefore);

        List<UserStatus> userStatusList = createStatusWithOneDate(allAttendance);
        if (!userStatusList.isEmpty()) {
            userStatusRepository.saveAll(userStatusList);
            userStatusList.sort(Comparator.comparing(UserStatus::getUserName));
        }
        return userStatusList;

    }

    public List<RangeStatus> createAndSaveRangeStatus(List<Attendance> allAttendance, String sFromDate, String sToDate) {

        LocalDate fromDate = stringToDate(sFromDate);
        LocalDate toDate = stringToDate(sToDate);

        List<RangeStatus> rangeStatusList = createRangeStatus(allAttendance, fromDate, toDate);
        if (!rangeStatusList.isEmpty()) {
            List<RangeStatus> rangeStatusListBefore = rangeStatusRepository.findByFromDateAndToDate(rangeStatusList.get(0).getFromDate(), rangeStatusList.get(0).getToDate());
            if (!rangeStatusListBefore.isEmpty())
                rangeStatusRepository.deleteAll();
            rangeStatusRepository.saveAll(rangeStatusList);
            rangeStatusList.sort(Comparator.comparing(RangeStatus::getUserName));
        }
        return rangeStatusList;
    }

    public List<RangeStatus> createRangeStatus(List<Attendance> allAttendance, LocalDate fromDate, LocalDate toDate) {

        List<UserStatus> userStatusList = new ArrayList<>();
        List<Attendance> attendances;
        Map<LocalDate, List<Attendance>> dateMap = new HashMap<>();
        int totalWorkingDate = 0;

        Iterator<Attendance> iterator = allAttendance.iterator();
        while (iterator.hasNext()) {
            Attendance attendance = iterator.next();
            LocalDate key = attendance.getRecordTime().toLocalDate();
            if (dateMap.containsKey(key))
                attendances = dateMap.get(key);
            else
                attendances = new ArrayList<>();

            attendances.add(attendance);
            dateMap.put(key, attendances);
        }

        // get list user status follow date
        for (Map.Entry<LocalDate, List<Attendance>> tmp : dateMap.entrySet()) {
            if (tmp.getKey().getDayOfWeek() != DayOfWeek.SATURDAY && tmp.getKey().getDayOfWeek() != DayOfWeek.SUNDAY) {
                totalWorkingDate++;
                List<UserStatus> userStatuses = createStatusWithOneDate(tmp.getValue());
                userStatusList.addAll(userStatuses);
            }
        }
        return createRangeStatusFromListUserStatus(userStatusList, fromDate, toDate, totalWorkingDate);
    }

    public List<RangeStatus> createRangeStatusFromListUserStatus(List<UserStatus> userStatuses, LocalDate fromDate, LocalDate toDate, int totalWorkingDate) {

        List<RangeStatus> rangeStatusList = new ArrayList<>();
        RangeStatus rangeStatus;
        Map<UserStatus, RangeStatus> maps = new HashMap<>();

        Iterator<UserStatus> iterator = userStatuses.iterator();
        while (iterator.hasNext()) {
            UserStatus userStatus = iterator.next();
            if (maps.containsKey(userStatus))
                rangeStatus = maps.get(userStatus);
            else
                rangeStatus = RangeStatus.builder().id(nextSequenceService.getNextSequence("rangeStatus")).fromDate(fromDate).toDate(toDate).
                        totalWorkingDate(totalWorkingDate).build();

            rangeStatus = createRangeStatusFromUserStatus(rangeStatus, userStatus);
            maps.put(userStatus, rangeStatus);
        }

        for (Map.Entry<UserStatus, RangeStatus> map : maps.entrySet()) {
            rangeStatusList.add(map.getValue());
        }
        return rangeStatusList;

    }

    public RangeStatus createRangeStatusFromUserStatus(RangeStatus rangeStatus, UserStatus userStatus) {

        float workingDate = rangeStatus.getWorkingDay() + userStatus.getWorkingDay();
        float late = rangeStatus.getLateSession();
        float absent = rangeStatus.getAbsentSession();
        String statusDescription = userStatus.getDescription();

        if (statusDescription.contains(StatusInfo.A.getDescription()))
            late += 1;
        if (statusDescription.contains(StatusInfo.P.getDescription()))
            late += 1;
        if (statusDescription.contains(StatusInfo.AA.getDescription()))
            absent += 1;
        if (statusDescription.contains(StatusInfo.AB.getDescription()))
            absent += 1;
        if (statusDescription.contains(StatusInfo.PA.getDescription()))
            absent += 1;
        if (statusDescription.contains(StatusInfo.PB.getDescription()))
            absent += 1;
        rangeStatus.setUserCode(userStatus.getUserCode());
        rangeStatus.setUserName(userStatus.getUserName());
        rangeStatus.setWorkingDay(workingDate);
        rangeStatus.setOffDay(absent / 2);
        rangeStatus.setLateSession(late);
        rangeStatus.setAbsentSession(absent);
        return rangeStatus;
    }


    public List<UserStatus> createStatusWithOneDate(List<Attendance> allAttendance) {

        String session = "";
        List<User> userWorkings = new ArrayList<>();
        LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        List<UserStatus> userStatusList = new ArrayList<>();

        LocalDate dateView = allAttendance.get(0).getRecordTime().toLocalDate();

        LocalDateTime as = dateView.atTime(5, 59, 59);
        LocalDateTime a0 = dateView.atTime(9, 16, 00);
        LocalDateTime aa = dateView.atTime(10, 01, 00);
        LocalDateTime ab = dateView.atTime(12, 16, 00);

        LocalDateTime ps = dateView.atTime(12, 59, 00);
        LocalDateTime p0 = dateView.atTime(14, 16, 00);
        LocalDateTime pa = dateView.atTime(15, 01, 00);
        LocalDateTime pb = dateView.atTime(17, 01, 00);

        if (dateView.isBefore(localDateTime.toLocalDate()) ||
                (localDateTime.isBefore(pb.withHour(23).withMinute(59)) && localDateTime.isAfter(ps)))
            session = "AB";
        else if (localDateTime.isBefore(ps))
            session = "A";


        List<User> userActives = getUserActives(dateView);
        Map<String, List<Attendance>> map = createMapUser(allAttendance);
        for (Map.Entry<String, List<Attendance>> mAttendance : map.entrySet()) {
            boolean hasWorking = false;

            LocalDateTime am = ab;
            LocalDateTime pm = pb;

            float workingDay = 0f;
            String status = "";
            String description = "";

//            System.out.println(mAttendance.getValue().get(1).getId());

            List<Attendance> attendanceList = mAttendance.getValue();
            // with each user then get time InOut fist.(am and pm)
            Iterator<Attendance> iterator = attendanceList.iterator();
            while (iterator.hasNext()) {
                LocalDateTime timeIO = iterator.next().getRecordTime();
                if (timeIO.isBefore(am) && timeIO.isBefore(ab) && timeIO.isAfter(as)) {
                    am = timeIO;
                    hasWorking = true;
                } else if (timeIO.isBefore(pm) && timeIO.isBefore(pb) && timeIO.isAfter(ps)) {
                    pm = timeIO;
                    hasWorking = true;
                }
            }

            User user = attendanceList.get(0).getUser();
            if (user == null)
                continue;

            // create status in the morning
            if (hasWorking && userActives.contains(user)) {
//            if (hasWorking ) {
                if (session.contains("A")) {
                    if (am.isBefore(a0) && am.isAfter(as)) {
                        status = StatusInfo.A0.name();
                        description = StatusInfo.A0.getDescription();
                        workingDay = 0.5f;
                    } else if (am.isBefore(aa) && (am.equals(a0) || am.isAfter(a0))) {
                        status = StatusInfo.A.name();
                        description = StatusInfo.A.getDescription();
                        workingDay = 0.5f;
                    } else if (am.isBefore(ab) && (am.equals(aa) || am.isAfter(aa))) {
                        status = StatusInfo.AA.name();
                        description = StatusInfo.AA.getDescription();
                        workingDay = 0f;
                    } else if (am.equals(ab)) {
                        status = StatusInfo.AB.name();
                        description = StatusInfo.AB.getDescription();
                        workingDay = 0f;
                    }
                }
                // create status in the afternoon
                if (session.contains("B")) {
                    // afternoon
                    if (pm.isBefore(p0) && pm.isAfter(ps)) {
                        status += StatusInfo.P0.name();
                        description += StatusInfo.P0.getDescription();
                        workingDay += 0.5f;
                    }
                    if (pm.isBefore(pa) && (pm.equals(p0) || pm.isAfter(p0))) {
                        status += StatusInfo.P.name();
                        description += StatusInfo.P.getDescription();
                        workingDay += 0.5f;
                    } else if (pm.isBefore(pb) && (pm.equals(pa) || pm.isAfter(pa))) {
                        status += StatusInfo.PA.name();
                        description += StatusInfo.PA.getDescription();
                        workingDay += 0f;
                    } else if (pm.equals(pb)) {
                        status += StatusInfo.PB.name();
                        description += StatusInfo.PB.getDescription();
                        workingDay += 0f;
                    }
                }

                UserStatus userStatus = UserStatus.builder().id(nextSequenceService.getNextSequence("userStatus"))
                        .userCode(user.getDeviceUserId())
                        .userName(user.getName())
                        .recordDate(dateView)
                        .workingDay(workingDay).status(status).description(description)
                        .build();
                userStatusList.add(userStatus);
                userWorkings.add(user);
            }
        }
        List<UserStatus> userAbsents = createUserStatusAbsent(userWorkings, userActives, session, dateView);
        if (!userAbsents.isEmpty())
            userStatusList.addAll(userAbsents);
        return userStatusList;
    }

    public List<UserStatus> createUserStatusAbsent(List<User> userWorkings, List<User> userActives, String session, LocalDate localDate) {

        List<UserStatus> userStatusList = new ArrayList<>();

        Iterator<User> iterator = userActives.iterator();

        while (iterator.hasNext()) {
            String status = "";
            String description = "";
            User user = iterator.next();

            if (!userWorkings.contains(user)) {
                if (session.contains("A")) {
                    status = StatusInfo.AB.name();
                    description = StatusInfo.AB.getDescription();
                }
                if (session.contains("B")) {
                    status += StatusInfo.PB.name();
                    description += StatusInfo.PB.getDescription();
                }

                UserStatus userStatus = UserStatus.builder().id(nextSequenceService.getNextSequence("userStatus"))
                        .userCode(user.getDeviceUserId())
                        .userName(user.getName())
                        .recordDate(localDate)
                        .workingDay(0f).status(status).description(description)
                        .build();
                userStatusList.add(userStatus);
            }

        }
        return userStatusList;
    }

    public Map<String, List<Attendance>> createMapUser(List<Attendance> attendances) {
        Map<String, List<Attendance>> map = new HashMap<>();
        List<Attendance> attendanceList;
        Iterator<Attendance> iterator = attendances.iterator();
        while (iterator.hasNext()) {
            Attendance attendance = iterator.next();
            String userCode = attendance.getDeviceUserId();
            if (map.containsKey(userCode))
                attendanceList = map.get(userCode);
            else
                attendanceList = new ArrayList<>();

            attendanceList.add(attendance);
            map.put(userCode, attendanceList);
        }
        return map;
    }

    public List<User> getUserActives(LocalDate localDate) {
        List<User> userActives = new ArrayList<>();
        List<Employee> employeeList = employeeRepository.findAll();
        Iterator<Employee> iterator = employeeList.iterator();
        while (iterator.hasNext()) {
            Employee employee = iterator.next();
            LocalDate lastWorkingDate = employee.getLastWorkingDate();
            if (lastWorkingDate == null || lastWorkingDate.isAfter(localDate) || employee.isActive()) {
                User user = employee.getUser();
                if (user != null) {
                    userActives.add(user);
                    System.out.println(user.getName() + "- " + user.getDeviceUserId());
                }
            }
        }
        return userActives;
    }

    public List<UserStatus> getUserStatus4Filter(String dateFilter, String status, String name) {

        LocalDate localDate = stringToDate(dateFilter);
        List<UserStatus> userStatusList = userStatusRepository.findByRecordDate(localDate);
        if (status.isEmpty() && name.isEmpty()) {
            userStatusList.sort(Comparator.comparing(UserStatus::getUserName));
            return userStatusList;
        } else if (status.isEmpty() && !name.isEmpty()) {
            return userStatusList.stream()
                    .filter(tmp -> tmp.getUserName().toLowerCase().contains(name.toLowerCase()))
                    .sorted(Comparator.comparing(UserStatus::getUserName))
                    .collect(Collectors.toList());
        } else if (!status.isEmpty() && name.isEmpty()) {
            return userStatusList.stream().filter(tmp -> tmp.getStatus().equals(status))
                    .sorted(Comparator.comparing(UserStatus::getUserName))
                    .collect(Collectors.toList());
        } else
            return userStatusList.stream().filter(tmp -> tmp.getStatus().equals(status))
                    .filter(tmp -> tmp.getUserName().toLowerCase().contains(name.toLowerCase()))
                    .sorted(Comparator.comparing(UserStatus::getUserName))
                    .collect(Collectors.toList());

    }

    public List<RangeStatus> getRangeStatus4Filter(String sFromDate, String sToDate, String name) {

        LocalDate fromDate = stringToDate(sFromDate);
        LocalDate toDate = stringToDate(sToDate);

        List<RangeStatus> rangeStatusList = rangeStatusRepository.findByFromDateAndToDate(fromDate, toDate);
        if (name.isEmpty()) {
            rangeStatusList.sort(Comparator.comparing(RangeStatus::getUserName));
            return rangeStatusList;
        } else {
            return rangeStatusList.stream()
                    .filter(tmp -> tmp.getUserName().toLowerCase().contains(name.toLowerCase()))
                    .sorted(Comparator.comparing(RangeStatus::getUserName))
                    .collect(Collectors.toList());
        }
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
