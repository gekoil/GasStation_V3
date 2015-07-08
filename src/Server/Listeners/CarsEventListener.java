package Listeners;

import BL.Car;
import DAL.Transaction;

public interface CarsEventListener {
	void getFueled(Car c, Transaction t);
	void getWashed(Car c, Transaction t);
}