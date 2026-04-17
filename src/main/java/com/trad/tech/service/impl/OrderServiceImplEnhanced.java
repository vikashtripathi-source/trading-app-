package com.trad.tech.service.impl;

import com.trad.tech.model.Order;
import com.trad.tech.repository.OrderRepository;
import com.trad.tech.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImplEnhanced implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_KEY_PREFIX = "order:";
    private static final String USER_ORDERS_PREFIX = "user:orders:";

    @Override
    public Order createOrder(Order order) {
        log.info("Creating new order: {}", order);

        // Set creation date and ID if not set
        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }
        if (order.getCreatedDate() == null) {
            order.setCreatedDate(LocalDateTime.now());
        }
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }

        // Save to MongoDB (existing synchronous behavior)
        Order savedOrder = orderRepository.save(order);

        // Publish to Kafka for async processing
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("orders", savedOrder);
                log.info("Published order creation to Kafka: {}", savedOrder.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to publish order creation to Kafka: {}", e.getMessage());
        }

        // Evict user orders cache
        evictUserOrdersCache(order.getUserId());

        return savedOrder;
    }

    @Override
    public List<Order> getUserOrders(String userId, String status) {
        log.info("Getting orders for user: {}, status: {}", userId, status);
        String cacheKey = USER_ORDERS_PREFIX + userId + ":" + (status != null ? status : "all");

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<Order> cachedOrders = (List<Order>) redisTemplate.opsForValue().get(cacheKey);
                if (cachedOrders != null) {
                    log.info("Cache hit for user orders: {}, status: {}", userId, status);
                    return cachedOrders;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for user {} orders: {}", userId, e.getMessage());
        }

        // Fallback to MongoDB
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByUserIdAndStatus(userId, orderStatus);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}, returning all orders for user", status);
                orders = orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
            }
        } else {
            orders = orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
        }

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, orders, java.time.Duration.ofMinutes(2));
                log.info("Cached user orders for user: {}, status: {}", userId, status);
            }
        } catch (Exception e) {
            log.warn("Failed to cache user orders for user {}: {}", userId, e.getMessage());
        }

        return orders;
    }

    @Override
    public Order getOrderById(String id) {
        log.info("Getting order by ID: {}", id);
        String cacheKey = ORDER_KEY_PREFIX + id;

        try {
            if (redisTemplate != null) {
                Order cachedOrder = (Order) redisTemplate.opsForValue().get(cacheKey);
                if (cachedOrder != null) {
                    log.info("Cache hit for order ID: {}", id);
                    return cachedOrder;
                }
            }
        } catch (Exception e) {
            log.warn("Redis cache error for order ID {}: {}", id, e.getMessage());
        }

        // Fallback to MongoDB
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        // Cache the result
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, order, java.time.Duration.ofMinutes(5));
                log.info("Cached order by ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to cache order by ID {}: {}", id, e.getMessage());
        }

        return order;
    }

    @Override
    public Order cancelOrder(String id) {
        log.info("Cancelling order: {}", id);

        Order order = getOrderById(id);

        // Only allow cancellation of pending orders
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setExecutedDate(LocalDateTime.now());

        // Save to MongoDB
        Order updatedOrder = orderRepository.save(order);

        // Publish to Kafka
        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send("order-cancellations", updatedOrder);
                log.info("Published order cancellation to Kafka: {}", updatedOrder.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to publish order cancellation to Kafka: {}", e.getMessage());
        }

        // Update cache
        updateOrderCache(updatedOrder);
        evictUserOrdersCache(order.getUserId());

        return updatedOrder;
    }

    @Override
    public List<Order> getPendingOrders(String userId) {
        log.info("Getting pending orders for user: {}", userId);

        return getUserOrders(userId, "PENDING");
    }

    // Cache management methods
    private void updateOrderCache(Order order) {
        try {
            if (redisTemplate != null) {
                String cacheKey = ORDER_KEY_PREFIX + order.getId();
                redisTemplate.opsForValue().set(cacheKey, order, java.time.Duration.ofMinutes(5));
                log.info("Updated cache for order ID: {}", order.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to update cache for order ID {}: {}", order.getId(), e.getMessage());
        }
    }

    private void evictUserOrdersCache(String userId) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(USER_ORDERS_PREFIX + userId + ":*"));
                log.info("Evicted cache for user orders: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for user orders {}: {}", userId, e.getMessage());
        }
    }

    public void evictOrderCache(String id) {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(ORDER_KEY_PREFIX + id);
                log.info("Evicted cache for order ID: {}", id);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache for order ID {}: {}", id, e.getMessage());
        }
    }

    public void evictAllOrderCache() {
        try {
            if (redisTemplate != null) {
                redisTemplate.delete(redisTemplate.keys(ORDER_KEY_PREFIX + "*"));
                redisTemplate.delete(redisTemplate.keys(USER_ORDERS_PREFIX + "*"));
                log.info("Evicted all order cache");
            }
        } catch (Exception e) {
            log.warn("Failed to evict all order cache: {}", e.getMessage());
        }
    }
}
