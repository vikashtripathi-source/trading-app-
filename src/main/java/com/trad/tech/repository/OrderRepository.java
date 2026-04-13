package com.trad.tech.repository;

import com.trad.tech.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    List<Order> findByUserId(String userId);
    
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
    
    Optional<Order> findByIdAndUserId(String id, String userId);
    
    List<Order> findBySymbol(String symbol);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByUserIdOrderByCreatedDateDesc(String userId);
}
