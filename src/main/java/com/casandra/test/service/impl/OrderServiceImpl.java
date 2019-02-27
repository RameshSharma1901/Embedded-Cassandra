package com.casandra.test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.casandra.test.domain.cassandra.Order;
import com.casandra.test.model.SaveRequest;
import com.casandra.test.repository.OrderRepository;
import com.casandra.test.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public boolean save(SaveRequest request) {
		Order order = new Order();
		order.setOrderID(request.getOrderId());
		order.setDiscount(request.getDiscount());
		order.setAmount(request.getAmount());

		orderRepository.save(order);

		return true;
	}

}
