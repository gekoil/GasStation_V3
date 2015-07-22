package BL;

import java.io.IOException;
import java.util.logging.FileHandler;

import Annotations.Loggable;

public class CleaningService {
	private int numOfTeams;
	private int price;
	private int secondsPerAutoClean;
	private AutoClean autoClean;
	private ManualClean[] manualClean;
	private int numOfTeamsFree;
	private FileHandler handler;
	
	public CleaningService(int numOfTeams, int price, int secondsPerAutoClean) {
		this.numOfTeams = numOfTeams;
		this.price = price;
		this.secondsPerAutoClean = secondsPerAutoClean;
		autoClean = new AutoClean();
		manualClean = new ManualClean[numOfTeams];
		for (int i = 0; i < numOfTeams; i++) {
			manualClean[i] = new ManualClean(i+1);
		}
		numOfTeamsFree = numOfTeams;

		initLogger();
	}

	public CleaningService() {
		autoClean = new AutoClean();
		initLogger();
	}

	public int getNumOfTeams() {
		return numOfTeams;
	}
	public void setNumOfTeams(int numOfTeams) {
		this.numOfTeams = numOfTeams;
		manualClean = new ManualClean[numOfTeams];
		for (int i = 0; i < numOfTeams; i++) {
			manualClean[i] = new ManualClean(i+1);
		}
		numOfTeamsFree = numOfTeams;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getSecondsPerAutoClean() {
		return secondsPerAutoClean;
	}
	public void setSecondsPerAutoClean(int secondsPerAutoClean) {
		this.secondsPerAutoClean = secondsPerAutoClean;
	}
	public AutoClean getAutoClean() {
		return autoClean;
	}
	public void setAutoClean(AutoClean autoClean) {
		this.autoClean = autoClean;
	}
	public ManualClean[] getManualClean() {
		return manualClean;
	}
	public void setManualClean(ManualClean[] manualClean) {
		this.manualClean = manualClean;
	}
	public int getNumOfTeamsFree() {
		return numOfTeamsFree;
	}
	public void setNumOfTeamsFree(int numOfTeamsFree) {
		this.numOfTeamsFree = numOfTeamsFree;
	}
	@Loggable(logMessage = "Car get auto cleand")
	public boolean carGetAutoClean() {
		return true;
	}
	@Loggable(logMessage = "Car get manual cleand")
	public boolean carGetManualClean() {
		return true;
	}
	@Override
	public String toString() {
		return "CleaningService [numOfTeams=" + numOfTeams + ", price=" + price
				+ ", secondsPerAutoClean=" + secondsPerAutoClean + "]";
	}

	private void initLogger() {
		try {
			this.handler = new FileHandler("Cleaning Service Log.txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
