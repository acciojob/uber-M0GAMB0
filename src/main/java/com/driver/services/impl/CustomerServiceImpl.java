package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SortClass implements Comparator<Driver> {
	@Override
	public int compare(Driver o1, Driver o2) {
		return o1.getDriverId() - o2.getDriverId();
	}
}
@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		customerRepository2.save(customer);
		//Save the customer in database
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> drivers=driverRepository2.findAll();
		Collections.sort(drivers,new SortClass());
		Customer currCustomer=customerRepository2.findById(customerId).get();
		Driver currDriver = null;
		for(Driver driver:drivers){
			if(driver.getCab().isAvailable()){
				currDriver=driver;
				break;
			}
		}
		if(currDriver==null){
			throw new Exception("No cab available!");
		}

		currDriver.getCab().setAvailable(false);

		TripBooking tripBooking=new TripBooking();
		tripBooking.setCustomer(currCustomer);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setBill(currDriver.getCab().getPerKmRate()*distanceInKm);
		tripBooking.setTripStatus(TripStatus.CONFIRMED);
		tripBooking.setDriver(currDriver);
		driverRepository2.save(currDriver);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();

		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBooking.getDriver().getCab().setAvailable(true);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();

		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBooking.getDriver().getCab().setAvailable(true);
	}
}
