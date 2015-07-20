package BL;

import java.util.concurrent.locks.ReentrantLock;

import Annotations.Loggable;

public class AutoClean extends ReentrantLock {
	private static final long serialVersionUID = 1L;
	
	public AutoClean() {
	}
	// this method performs the auto cleaning process!
	@Loggable(logMessage = "Cleand car")
	public boolean autoClean(boolean gasStationClosing, Car car, int secondsPerAutoClean, CleaningService cs, GasStation gs) {
		boolean continueToManualClean = true;
		// start the auto-cleaning process and lock the object
		lock();
		cs.carGetAutoClean();
		// if closing, don't do autoClean and don't go to the ManualClean in the future!
		if (gasStationClosing) {			
			unlock();
			return false;
		}
		try {
			Thread.sleep(secondsPerAutoClean * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		unlock();	
		return continueToManualClean;
	}  // autoClean
}
