package com.casandra.test.service;

import com.casandra.test.model.SaveRequest;

public interface OrderService {

	public boolean save(SaveRequest request);

}
