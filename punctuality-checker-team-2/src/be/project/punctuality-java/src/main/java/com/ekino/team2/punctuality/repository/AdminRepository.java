package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdminRepository extends MongoRepository<Admin, Long> {
    List<Admin> findByGoogleAccount(String googleAccount);

}
