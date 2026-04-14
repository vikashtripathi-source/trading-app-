package com.trad.tech.repository;

import com.trad.tech.model.Portfolio;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

  Optional<Portfolio> findByUserId(String userId);

  void deleteByUserId(String userId);
}
