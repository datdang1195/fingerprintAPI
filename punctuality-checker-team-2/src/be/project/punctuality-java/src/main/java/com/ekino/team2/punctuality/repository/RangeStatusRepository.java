package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.model.RangeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface RangeStatusRepository extends MongoRepository<RangeStatus, Long> {

    List<RangeStatus> findByFromDateAndToDate(LocalDate fromDate, LocalDate toDate);
}
