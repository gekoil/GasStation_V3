package BL;

import java.util.concurrent.locks.ReentrantLock;

import UI.GasStationUI;

public class AutoClean extends ReentrantLock {
	private static final long serialVersionUID = 1L;
	
	public AutoClean() {
	}
	// this method performs the auto cleaning process!
	public boolean autoClean(boolean gasStationClosing, Car car, int secondsPerAutoClean, CleaningService cs, GasStation gs) {
		boolean continueToManualClean = true;
		// start the auto-cleaning process and lock the object
		lock();
		// if closing, don't do autoClean and don't go to the ManualClean in the future!
		if (gasStationClosing) {			
			unlock();
			return false;
		}
		GasStationUI.autoCleaning(car, secondsPerAutoClean * 1000, gs);
		GasStationUI.autoCleaning(car, secondsPerAutoClean * 1000, car);
		GasStationUI.autoCleaning(car, secondsPerAutoClean * 1000, cs);
		try {
			Thread.sleep(secondsPerAutoClean * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		unlock();	
		return continueToManualClean;
	}  // autoClean
}
