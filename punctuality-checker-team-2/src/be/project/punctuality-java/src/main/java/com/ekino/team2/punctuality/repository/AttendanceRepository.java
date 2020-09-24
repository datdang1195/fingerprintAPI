package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.entity.Attendance;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, ObjectId> {
    List<Attendance> findAttendanceByRecordTimeBetweenOrderByDeviceUserId(LocalDateTime from, LocalDateTime to);

    Optional<Attendance> findFirstByOrderByRecordTimeDesc();
}
