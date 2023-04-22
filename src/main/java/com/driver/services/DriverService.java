package com.driver.services;

import com.driver.model.Driver;
import com.driver.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface DriverService {

		public void register(String mobile, String password);
		public void removeDriver(int driverId);
		public void updateStatus(int driverId);
}
