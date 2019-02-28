package com.casandra.test.domain.cassandra;

import java.io.Serializable;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("ORDERS")
@Getter
@Setter
public class Order implements Serializable {

	@Id
	@PrimaryKeyColumn(name = "orderID", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
	private String orderID;

	private String orderDetails;

	private String transactionFlowType;

}
