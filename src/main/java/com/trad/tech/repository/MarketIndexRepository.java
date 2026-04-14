package com.trad.tech.repository;

import com.trad.tech.model.MarketIndex;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MarketIndexRepository extends MongoRepository<MarketIndex, String> {

  MarketIndex findByName(String name);

  List<MarketIndex> findAll();
}
