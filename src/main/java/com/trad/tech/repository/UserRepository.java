package com.trad.tech.repository;

import com.trad.tech.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> findByIdAndIsActive(String id, boolean isActive);

  Optional<User> findByEmailAndIsActive(String email, boolean isActive);
}
