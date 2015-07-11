package BL;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import UI.GasStationUI;

public class MainFuelPool extends ReentrantLock {
	private static final long serialVersionUID = 1L;
	private int maxCapacity;
	private int currentCapacity;
	private Condition isEligibleToFillUpMainPool = this.newCondition();
	private boolean waitingToFillMainPool;
	
	public MainFuelPool(int maxCapacity, int currentCapacity) {
		this.maxCapacity = maxCapacity;
		this.currentCapacity = currentCapacity;
	}

	public MainFuelPool() {
	}

	public synchronized void fillMainFuelPool(GasStation gs, int numOfLitersToFill) {
		// if CurrentCapacity + numOfLitersToFill exceeds the max capacity, fill up to max
		if (this.getMaxCapacity() - this.getCurrentCapacity() < numOfLitersToFill)
			numOfLitersToFill = this.getMaxCapacity() - this.getCurrentCapacity();
		if (numOfLitersToFill == 0) {
			GasStationUI.mainFuelPoolIsFull(gs);
			gs.fireTheMainFuelIsFull();
			return;
		}
		while(gs.getNumOfCarsFuelingUpCurrently() > 0) {
			try {
				lock();
				waitingToFillMainPool = true;
				this.isEligibleToFillUpMainPool.await();
				unlock();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		waitingToFillMainPool = false;
		gs.setFillingMainFuelPool(true);
		gs.fireFillingTheMainFuel();
		// if numOfLitersToFill exceeds the max capacity, throw an exception
		if (numOfLitersToFill + getCurrentCapacity() > getMaxCapacity())
			throw new RuntimeException("The number of liters you are trying to fill, exceeds the maximum fuel pool capacity!");
		GasStationUI.fillMainFuelPool(numOfLitersToFill, gs);		
		// time to fill the pool is numOfLitersToFill*30 (for example: 100 liters = 5ms)
		try { 
			Thread.sleep((long)numOfLitersToFill * 100); 
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		setCurrentCapacity(getCurrentCapacity() + numOfLitersToFill);
		gs.setFillingMainFuelPool(false);
		gs.finishedFillTheMainFuel();
		// notify all the threads, that are waiting to fuel up
		for (int i = 0; i < gs.getPumps().length; i++) {
			gs.getPumps()[i].lock();
			gs.getPumps()[i].getIsEligibleToFuelUp().signalAll();
			gs.getPumps()[i].unlock();
		}
		GasStationUI.finishedFillingUpTheMainFuelPool(gs);
		GasStationUI.currentFuelState(getCurrentCapacity(), gs);
		gs.fireTheMainFuelPoolCapacity();
		
	}  // fillMainFuelPool
	
	public boolean isWaitingToFillMainPool() {
		return waitingToFillMainPool;
	}
	public void setWaitingToFillMainPool(boolean waitingToFillMainPool) {
		this.waitingToFillMainPool = waitingToFillMainPool;
	}
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public Condition getIsEligibleToFillUpMainPool() {
		return isEligibleToFillUpMainPool;
	}
	public void setIsEligibleToFillUpMainPool(Condition isEligibleToFillUpMainPool) {
		this.isEligibleToFillUpMainPool = isEligibleToFillUpMainPool;
	}
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public int getCurrentCapacity() {
		return currentCapacity;
	}
	// this method is synchronized, since many threads access this method simultaneously
	public synchronized void setCurrentCapacity(int currentCapacity) {
		this.currentCapacity = currentCapacity;
	}
	@Override
	public String toString() {
		return "MainFuelPool [maxCapacity=" + maxCapacity
				+ ", currentCapacity=" + currentCapacity + "]";
	}
}
