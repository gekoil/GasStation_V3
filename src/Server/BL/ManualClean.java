package BL;

import UI.GasStationUI;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;

// ManualClean is a lock and once it is held, it can't be held by another car simultaneously
public class ManualClean extends ReentrantLock {
	private static final long serialVersionUID = 1L;
	private int teamNum;
	private FileHandler handler;

	public ManualClean(int num) {
		this.teamNum = num;
		try {
			this.handler = new FileHandler("ManualClean"+this.teamNum+".txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ManualClean() {
	}

	public int getTeamNum() {
		return teamNum;
	}
	public void setTeamNum(int teamNum) {
		this.teamNum = teamNum;
	}

	// this method performs the manual cleaning process!
	public void manualClean(Car car, CleaningService cs, GasStation gs) {
		lock();
		// team work takes random time
		int cleanTime = (int) (Math.random() * 10000);
		// writing to the appropriate files (GasStation,Car,CleanService,ManualCleanService)
		GasStationUI.manualCleaning(car, cleanTime, teamNum, gs);
		GasStationUI.manualCleaning(car, cleanTime, teamNum, car);
		GasStationUI.manualCleaning(car, cleanTime, teamNum, cs);
		GasStationUI.manualCleaning(car, cleanTime, teamNum, this);
		try {
			Thread.sleep(cleanTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			unlock();
		}
	}  // manualClean
}
