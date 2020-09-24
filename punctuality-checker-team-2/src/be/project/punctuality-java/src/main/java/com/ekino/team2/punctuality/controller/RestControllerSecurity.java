package com.ekino.team2.punctuality.controller;


import com.ekino.team2.punctuality.entity.Admin;
import com.ekino.team2.punctuality.entity.Attendance;
import com.ekino.team2.punctuality.entity.Employee;
import com.ekino.team2.punctuality.entity.UserStatus;
import com.ekino.team2.punctuality.model.AttendanceResponse;
import com.ekino.team2.punctuality.model.LoginResponse;
import com.ekino.team2.punctuality.model.RangeStatus;
import com.ekino.team2.punctuality.model.Request;
import com.ekino.team2.punctuality.model.Response;
import com.ekino.team2.punctuality.repository.AdminRepository;
import com.ekino.team2.punctuality.repository.AttendanceRepository;
import com.ekino.team2.punctuality.repository.EmployeeRepository;
import com.ekino.team2.punctuality.service.AttendanceService;
import com.ekino.team2.punctuality.service.DailyReport;
import com.ekino.team2.punctuality.service.EmployeeService;
import com.ekino.team2.punctuality.service.GoogleService;
import com.ekino.team2.punctuality.service.NextSequenceService;
import com.ekino.team2.punctuality.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class RestControllerSecurity {

    @Autowired
    private GoogleService googleService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StatusService statusService;

    @Autowired
    NextSequenceService nextSequenceService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    DailyReport dailyReport;

    @GetMapping("/login")
    public LoginResponse authenticateUser(@RequestHeader("access_token") String gwt) {
        String jwt = "";
        Optional<String> email = googleService.getGoogleAccount(gwt);
        if (email.isPresent()) {
            jwt = googleService.generateToken(email.get());
        }
        return new LoginResponse(jwt);
    }

    @PostMapping("/dailystatus")
    public Response createDailyStatus(@RequestBody Request requestBody) {
        List<Attendance> attendances = attendanceService.getAttendance(requestBody);
        if (attendances.isEmpty()) {
            // get attendance latest
            return Response.builder().total(0).build();
        }
        List<UserStatus> userStatusList = statusService.createDailyStatus(attendances);
        return Response.builder().statusList(userStatusList).total(userStatusList.size()).build();
    }

    @PostMapping("/rangestatus")
    public Response createRangeStatus(@RequestBody Request requestBody) {
        List<Attendance> attendances = attendanceService.getAttendance(requestBody);
        if (attendances.isEmpty()) {
            return Response.builder().total(0).build();
        }
        List<RangeStatus> rangeStatusList = statusService.createAndSaveRangeStatus(attendances, requestBody.getFromDate(), requestBody.getToDate());
        return Response.builder().rangeStatusList(rangeStatusList).total(rangeStatusList.size()).build();

    }

    @GetMapping("/employees")
    public Response getemployee() {
        List<Employee> employees = employeeService.getEmployees();
        if (!employees.isEmpty())
            employees.sort(Comparator.comparing(employee -> (employee.getUser().getName())));
        return Response.builder().employees(employees).total(employees.size()).build();
    }

    @PostMapping("/updateactive")
    public Response updateActive(@RequestBody Request request) {
        if (request.getEmployees() == null)
            return Response.builder().message("Please put employee to update").build();

        List<Employee> employees = employeeService.updateEmployee(request.getEmployees());
        if (!employees.isEmpty())
            employees.sort(Comparator.comparing(employee -> (employee.getUser().getName())));

        return Response.builder().employees(employees).total(employees.size()).message("Update sucess").build();
    }

    @GetMapping("/dailystatuslatest")
    public Response dailyStatusLatest() {

        Request requestBody = Request.builder().build();
        attendanceRepository.findFirstByOrderByRecordTimeDesc().ifPresent(tmp -> requestBody.setFromDate(tmp.getRecordTime().toLocalDate().toString()));
        List<Attendance> attendances = attendanceService.getAttendance(requestBody);
        List<UserStatus> userStatusList = statusService.createDailyStatus(attendances);
        return Response.builder().statusList(userStatusList).total(userStatusList.size()).build();
    }

    @GetMapping("/exportdaily")
    public ResponseEntity<InputStreamResource> exportDaily(@RequestParam("dateFilter") String dateFilter, @RequestParam("status") String status, @RequestParam("name") String name) {
        List<UserStatus> userStatusList = statusService.getUserStatus4Filter(dateFilter, status, name);
        String filename = "DailyReport-"+dateFilter.replace("-", "") + "-" + System.currentTimeMillis();
        ByteArrayInputStream in = dailyReport.exportDailyExcel(userStatusList, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename= " + filename + ".xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @GetMapping("/exportrange")
    public ResponseEntity<InputStreamResource> exportRange(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate, @RequestParam("name") String name) {
        List<RangeStatus> rangeStatusList = statusService.getRangeStatus4Filter(fromDate, toDate, name);
        String filename = "RangeReport-"+fromDate.replace("-", "") + "- " + toDate.replace("-", "") + "-" + System.currentTimeMillis();
        ByteArrayInputStream in = dailyReport.exportRangeExcel(rangeStatusList, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename= " + filename + ".xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @GetMapping("/exportemployee")
    public ResponseEntity<InputStreamResource> exportEmployee(@RequestParam("name") String name) {
        List<Employee> employeeList = employeeRepository.findAll();
        List<Employee> employeesFilter;
        String filename = "EmployeeReport" + "-" + System.currentTimeMillis();
        if (name.isEmpty())
            employeesFilter = employeeList.stream().sorted(Comparator.comparing(employee -> employee.getUser().getName())).collect(Collectors.toList());
        else
            employeesFilter = employeeList.stream()
                    .filter(tmp -> tmp.getUser().getName().toLowerCase().contains(name))
                    .sorted(Comparator.comparing(employee -> employee.getUser().getName()))
                    .collect(Collectors.toList());

        ByteArrayInputStream in = dailyReport.exportEmployee(employeesFilter, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename= " + filename + ".xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    // API for testing
    @GetMapping("/getuserall")
    public List<Admin> getUserall() {
        return adminRepository.findAll();
    }

    @PostMapping("/createuser")
    public String createUser(@RequestBody Request request) {
        String email = request.getEmail();
        try {
            if (StringUtils.hasText(email)) {
                Admin admin = Admin.builder().fullName(email).googleAccount(email).role("SuperUser").
                        id(nextSequenceService.getNextSequence("Admin")).build();
                adminRepository.save(admin);
            }
        } catch (Exception e) {
            return "Create admin fail with email : " + email + e.getMessage();
        }
        return "Create admin suscess with email : " + email;
    }

    @GetMapping("/getuser")
    public List<Admin> getUser(@RequestHeader("email") String email) {
        return adminRepository.findByGoogleAccount(email);

    }

    @PostMapping("/attendances")
    public AttendanceResponse getAttendances(@RequestBody Request requestBody) {
        List<Attendance> attendances = attendanceService.getAttendance(requestBody);
        return AttendanceResponse.builder().attendances(attendances).total(attendances.size()).build();
    }

    @GetMapping("/updateemployee")
    public Response updateEmployee() {
        employeeService.updateEmployee();
        return Response.builder().message("update employee suscess").build();
    }

}
