package com.ekino.team2.punctuality.repository;

import com.ekino.team2.punctuality.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
}
