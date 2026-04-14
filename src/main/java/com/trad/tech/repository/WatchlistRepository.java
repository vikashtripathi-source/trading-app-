package com.trad.tech.repository;

import com.trad.tech.model.Watchlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends MongoRepository<Watchlist, String> {

  List<Watchlist> findByUserId(String userId);

  Optional<Watchlist> findByIdAndUserId(String id, String userId);

  List<Watchlist> findByUserIdAndIsDefault(String userId, boolean isDefault);

  void deleteByUserId(String userId);

  List<Watchlist> findByUserIdOrderByCreatedDateDesc(String userId);
}
