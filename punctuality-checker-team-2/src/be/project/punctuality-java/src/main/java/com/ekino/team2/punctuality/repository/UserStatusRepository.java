package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.entity.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserStatusRepository extends MongoRepository<UserStatus, Long> {
    List<UserStatus> findByRecordDate(LocalDate localDate);

}
