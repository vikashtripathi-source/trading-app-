package com.trad.tech.repository;

import com.trad.tech.model.MarketDataEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MarketDataRepository extends MongoRepository<MarketDataEntity, String> {

  Optional<MarketDataEntity> findBySymbol(String symbol);

  List<MarketDataEntity> findBySymbolIn(List<String> symbols);

  List<MarketDataEntity> findTop10ByOrderByChangeDesc();

  List<MarketDataEntity> findTop10ByOrderByChangeAsc();
}
