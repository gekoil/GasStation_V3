package BL;

import Annotations.Loggable;
import DAL.Entities.PumpsEntity;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;

// Pump is a lock and once it is held, it can't be held by another car simultaneously
public class Pump extends ReentrantLock{
	private static final long serialVersionUID = 1L;
	private int num;
	private GasStation station;
	private FileHandler handler;
	private Condition isEligibleToFuelUp = this.newCondition();
	
	
	public Pump(int num, GasStation gasStation) {
		this.num = num;
		this.setStation(gasStation);
		try {
			this.handler = new FileHandler("Pump_Number" + this.num + ".txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pump() {
	}

	// this function returns true if the queue on CleanService is shorter than on the pump
	public synchronized boolean checkWhichQueueIsShorter(CleaningService cs, Car car) { //, GasStation gs) {
		/** these 2 numbers are for making sure that a car goes to the queue with the 
		 * least amount of waiters, INCLUDING THE CARS HOLDING THE LOCK, so that 
		 * we reduce the total waiting time in the gas station! */
		int isAutoCleanLocked = 0;
		int isPumpLocked = 0;
		if (cs.getAutoClean().isLocked())
			isAutoCleanLocked = 1;
		if (isLocked())
			isPumpLocked = 1;
		if (getQueueLength() + isPumpLocked > cs.getAutoClean().getQueueLength() + isAutoCleanLocked 
			&& !car.isCleanedUp() && !station.isGasStationClosing()) {
			return true;
		}
		return false;
	}
	
	public void pumpFuelUp(Car car, MainFuelPool mfpool) {//, GasStation gs) {
		// locks the needed pump in order to fuel up, so that another car can't hold it
		lock();
		int fuelingUpTime = car.getNumOfLiters() * 100;
		// wait while filling the main fuel pool
		while (station.isFillingMainFuelPool() || station.getMfpool().isWaitingToFillMainPool()) {
			try {
				this.isEligibleToFuelUp.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		station.setNumOfCarsFuelingUpCurrently(station.getNumOfCarsFuelingUpCurrently()+1);
		// this is for writing to the appropriate files! (Car, Pump, GasStation) - Logger
		try {
			// it takes NumOfLiters * 100 ms to fuel up. For 30 liters it will take 3[sec]
			Thread.sleep(fuelingUpTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		station.setNumOfCarsFuelingUpCurrently(station.getNumOfCarsFuelingUpCurrently()-1);
		unlock();	
		// if the MainFuelPool is waiting to get filled up, signal it on the condition
		if (station.getNumOfCarsFuelingUpCurrently() == 0) {
			station.getMfpool().lock();
				station.getMfpool().getIsEligibleToFillUpMainPool().signalAll();
			station.getMfpool().unlock();
		}
	}  // pumpFuelUp
	
	@Loggable(logMessage = "Fueld up car")
	private void updateCarFueld(Car c) {
		c.setFueledUp(true);
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public Condition getIsEligibleToFuelUp() {
		return isEligibleToFuelUp;
	}
	
	public void setIsEligibleToFuelUp(Condition isEligibleToFuelUp) {
		this.isEligibleToFuelUp = isEligibleToFuelUp;
	}
	
	public PumpsEntity getEntity() {
		PumpsEntity entity = new PumpsEntity();
		entity.setId(this.getNum());
		entity.setStationId(this.station.getId());
		return entity;
	}
	
	@Loggable(logMessage = "Pump created")
	public void setStation(GasStation station) {
		this.station = station;
	}
}  // Pump
