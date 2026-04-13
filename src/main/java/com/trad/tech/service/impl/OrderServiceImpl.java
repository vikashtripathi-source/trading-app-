package com.trad.tech.service.impl;

import com.trad.tech.model.Order;
import com.trad.tech.repository.OrderRepository;
import com.trad.tech.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    
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
        
        return orderRepository.save(order);
    }
    
    @Override
    public List<Order> getUserOrders(String userId, String status) {
        log.info("Getting orders for user: {}, status: {}", userId, status);
        
        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                return orderRepository.findByUserIdAndStatus(userId, orderStatus);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}, returning all orders for user", status);
            }
        }
        
        return orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }
    
    @Override
    public Order getOrderById(String id) {
        log.info("Getting order by ID: {}", id);
        
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
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
        
        return orderRepository.save(order);
    }
    
    @Override
    public List<Order> getPendingOrders(String userId) {
        log.info("Getting pending orders for user: {}", userId);
        
        return orderRepository.findByUserIdAndStatus(userId, Order.OrderStatus.PENDING);
    }
}
