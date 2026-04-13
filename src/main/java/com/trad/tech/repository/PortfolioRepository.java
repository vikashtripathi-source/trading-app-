package com.trad.tech.repository;

import com.trad.tech.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    
    Optional<Portfolio> findByUserId(String userId);
    
    void deleteByUserId(String userId);
}
