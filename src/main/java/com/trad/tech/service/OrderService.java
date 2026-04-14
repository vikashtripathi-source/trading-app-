package com.trad.tech.service;

import com.trad.tech.model.Order;
import java.util.List;

public interface OrderService {
  Order createOrder(Order order);

  List<Order> getUserOrders(String userId, String status);

  Order getOrderById(String id);

  Order cancelOrder(String id);

  List<Order> getPendingOrders(String userId);
}
