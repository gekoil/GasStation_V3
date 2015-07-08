package UI;

import BL.Car;
import BL.GasStation;
import Listeners.StatisticEventListener;

import java.util.Vector;
import java.util.logging.Level;

public class GasStationUI {

	public static void fuelingUp(Car car, int fuelingUpTime, int pumpNum, Object param) {
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " is fueling up, for " + fuelingUpTime + "ms, at Pump #" + pumpNum, param);
	}

	public static void finishedfuelingUp(Car car, int fuelingUpTime, int pumpNum, Object param) {
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " finished fueling up, at Pump #" + pumpNum, param);
	}

	public static void autoCleaning(Car car, int autoCleanTime, Object param) {
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " is being cleaned automatically, for " + autoCleanTime + "ms", param);
	}

	public static void manualCleaning(Car car, int manualCleanTime, int teamNum, Object param) {
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " is being cleaned manually, for " + manualCleanTime + "ms by team #" + teamNum, param);
	}

	public static void emptyFuelPool(Car car, Object param) {
		//System.out.println("Car #" + car.getID() + " tries to fuel up while the main pool has less fuel than the car needs.");
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " tries to fuel up while the main pool has less fuel than the car needs.", param);
	}

	public static void currentFuelState(int currentCapacity, Object param) {
		GasStation.getLog().log(Level.INFO, "Liters in the main pool: " + currentCapacity, param);
	}

	public static void fillMainFuelPool(int numOfLitersToFill, Object param) {
		//System.out.println("Filling the Main Fuel Pool with: " + numOfLitersToFill + " Liters, for " + numOfLitersToFill*100 + "ms");
		GasStation.getLog().log(Level.INFO, "Filling the Main Fuel Pool with: " + numOfLitersToFill + " Liters, for " + numOfLitersToFill*100 + "ms", param);
	}

	public static void closeGasStation(Object param) {
		//System.out.println("The gas station is closing!");
		GasStation.getLog().log(Level.INFO, "The gas station is closing!", param);
	}

	public static void finishedFillingUpTheMainFuelPool(Object param) {
		//System.out.println("Finished filling up the Main Fuel Pool!");
		GasStation.getLog().log(Level.INFO, "Finished filling up the Main Fuel Pool!", param);
	}

	public static void carLeavingTheGasStation(Car car, Object param) {
		GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " is leaving the gas station!", param);
	}

	public static void statWillBeShown(Object param, Vector<StatisticEventListener> sl) {
		//System.out.println("The statistics will be shown after the last car leaves the gas station");
		GasStation.getLog().log(Level.INFO, "The statistics will be shown after the last car leaves the gas station", param);
		for(StatisticEventListener l : sl)
			l.ShowStatistics("The statistics will be shown after the last car leaves the gas station");
	}

	public static void showStatistics(GasStation gs, Object param) {
		//System.out.println(gs.getStatistics().toString());
		GasStation.getLog().log(Level.INFO, gs.getStatistics().toString(), param);
		for(StatisticEventListener l : gs.getStatisticsListeners())
			l.ShowStatistics(gs.getStatistics().toString());
	}

	public static void cantCloseWhileFillingMainPool(Object param, Vector<StatisticEventListener> sl) {
		//System.out.println("The gas station can't be closed while filling the main fuel pool.");
		GasStation.getLog().log(Level.INFO, "The gas station can't be closed while filling the main fuel pool. Try again later", param);
		for(StatisticEventListener l : sl)
			l.ShowStatistics("The gas station can't be closed while filling the main fuel pool.");
	}

	public static void mainFuelPoolIsFull(Object param) {
		//System.out.println("The Main Fuel Pool is full, therefore can't be filled up currently!");
		GasStation.getLog().log(Level.INFO, "The Main Fuel Pool is full, try to fill it up again later!", param);
	}
}
