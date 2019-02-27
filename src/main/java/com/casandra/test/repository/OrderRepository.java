
package com.casandra.test.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.casandra.test.domain.cassandra.Order;

@Repository
public interface OrderRepository extends CassandraRepository<Order> {

	@Query("select * from ORDER_DATA where orderID = ?0")
	public List<Order> findByOrderId(String orderID);

}
