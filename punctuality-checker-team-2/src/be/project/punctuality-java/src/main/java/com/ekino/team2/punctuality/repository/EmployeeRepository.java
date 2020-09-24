package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, Long> {
}
