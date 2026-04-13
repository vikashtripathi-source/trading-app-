package com.trad.tech.repository;

import com.trad.tech.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByIdAndIsActive(String id, boolean isActive);
    
    Optional<User> findByEmailAndIsActive(String email, boolean isActive);
}
