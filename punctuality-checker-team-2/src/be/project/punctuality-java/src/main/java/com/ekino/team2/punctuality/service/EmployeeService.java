package com.ekino.team2.punctuality.service;

import com.ekino.team2.punctuality.entity.Employee;
import com.ekino.team2.punctuality.entity.User;
import com.ekino.team2.punctuality.repository.EmployeeRepository;
import com.ekino.team2.punctuality.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public void updateEmployee() {
        List<Employee> employees = employeeRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Employee> newEmployees = new ArrayList<>();

        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (!employees.contains(Employee.builder().user(user).build())) {
                Employee employee = Employee.builder().id(nextSequenceService.getNextSequence("employee"))
                        .user(user).isActive(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                        .build();
                newEmployees.add(employee);
            }
        }
        if (!newEmployees.isEmpty())
            employeeRepository.saveAll(newEmployees);

    }

    public List<Employee> updateEmployee(List<Employee> employees) {
        Iterator<Employee> iterator = employees.iterator();
        while (iterator.hasNext()) {
            Employee employee = iterator.next();
            Long id = employee.getId();
            if (id != null)
                employeeRepository.findById(id).ifPresent(tmp -> {
                            tmp.setActive(employee.isActive());
                            tmp.setLastWorkingDate(employee.getLastWorkingDate());
                            employeeRepository.save(tmp);
                        }
                );
        }
        return employeeRepository.findAll();
    }
}
