package com.trad.tech.repository;

import com.trad.tech.model.Trade;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeRepository extends MongoRepository<Trade, String> {

  List<Trade> findByUserId(String userId);
}
