package com.trad.tech.repository;

import com.trad.tech.model.StockSymbol;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockSymbolRepository extends MongoRepository<StockSymbol, String> {

  List<StockSymbol> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(
      String symbol, String name);

  List<StockSymbol> findByActiveTrue();

  StockSymbol findBySymbol(String symbol);
}
