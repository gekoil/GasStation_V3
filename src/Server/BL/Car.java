package BL;

import DAL.Entities.CarsEntity;
import UI.GasStationUI;
import Annotations.DuringWash;

import java.io.IOException;
import java.util.logging.FileHandler;

// Car as a thread enters the GasStation, holds the locks (Pump/AutoClean/ManualClean)
// and performs the needed operations in the relevant classes
public class Car implements Runnable {
	private int id;
	private boolean wantCleaning;
	private int numOfLiters;
	private int pumpNum;
	private GasStation gs;
	private boolean fueledUp;
	private boolean cleanedUp;
	private FileHandler handler;
	private ClientsSocketInfo owner;

	public Car(int id, boolean wantCleaning, int numOfLiters, int pumpNum, GasStation gs) {
		this.id = id;
		this.wantCleaning = wantCleaning;
		this.numOfLiters = numOfLiters;
		this.pumpNum = pumpNum;
		this.gs = gs;
		fueledUp = false;
		cleanedUp = false;

		try {
			this.handler = new FileHandler("Car_ID"+this.id+" Log.txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(this.handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public Car() {
	}

	public void run() {
		try {
			while ((!cleanedUp || !fueledUp) && !gs.isGasStationClosing()) {
				if (!fueledUp && numOfLiters > 0)
					gs.fuelUp(this);
				if (!cleanedUp && wantCleaning)
					gs.cleanCar(this);
				if(!wantCleaning && (fueledUp || numOfLiters == 0))
					break;
				if(numOfLiters == 0 && cleanedUp)
					break;
			}
			// if the gas station is closing and haven't fueled up yet, go to fuel up!!!
			while (!fueledUp && numOfLiters > 0) {
				gs.fuelUp(this);
			}
			GasStationUI.carLeavingTheGasStation(this, gs);
			GasStationUI.carLeavingTheGasStation(this, this);
			gs.setNumOfCarsInTheGasStationCurrently(gs.getNumOfCarsInTheGasStationCurrently()-1);
			if (gs.getNumOfCarsInTheGasStationCurrently() == 0 && gs.isGasStationClosing()) {
				GasStationUI.showStatistics(gs, gs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClientCar toClientCar() {
		boolean needWash = false;
		if (!cleanedUp && wantCleaning)
			needWash = true;
		return new ClientCar(id, numOfLiters, needWash, pumpNum);
	}

	public int getID() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isWantCleaning() {
		return wantCleaning;
	}

	public void setWantCleaning(boolean wantCleaning) {
		this.wantCleaning = wantCleaning;
	}

	public int getNumOfLiters() {
		return numOfLiters;
	}

	public void setNumOfLiters(int numOfLiters) {
		this.numOfLiters = numOfLiters;
	}

	public int getPumpNum() {
		return pumpNum;
	}

	public void setPumpNum(int pumpNum) {
		this.pumpNum = pumpNum;
	}

	public boolean isFueledUp() {
		return fueledUp;
	}

	public void setFueledUp(boolean fueledUp) {
		this.fueledUp = fueledUp;
	}

	public boolean isCleanedUp() {
		return cleanedUp;
	}

	public void setCleanedUp(boolean cleanedUp) {
		this.cleanedUp = cleanedUp;
	}

	public void initLogger() {
		try {
			this.handler = new FileHandler("Car_ID"+this.id+" Log.txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(this.handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	@DuringWash
	public void readPaper() {
		System.out.println("Car #" + id + " is reading the paper");
	}

	@DuringWash
	public void playGame() {
		System.out.println("Car #" + id + " is playing a game");
	}

	@DuringWash
	public void talkOnPhone() {
		System.out.println("Car #" + id + " is talking on the phone");
	}

	@Override
	public String toString() {
		return "Car [id=" + id + ", wantCleaning=" + wantCleaning
				+ ", numOfLiters=" + numOfLiters + ", pumpNum=" + pumpNum + "]";
	}

	public ClientsSocketInfo getOwner() {
		return owner;
	}

	public void setOwner(ClientsSocketInfo owner) {
		this.owner = owner;
	}

	public void setGasStation(GasStation gs) {
		this.gs = gs;
	}

	public GasStation getGasStation() {
		return gs;
	}

	public CarsEntity toEntity() {
		CarsEntity entity = new CarsEntity();
		entity.setId(this.getID());
		entity.setWantCleaning(this.isWantCleaning());
		entity.setNumOfLiters(this.getNumOfLiters());
		entity.setPumpNum(this.getPumpNum());
		entity.setGs(this.getGasStation().getId());
		entity.setFueledUp(this.isFueledUp());
		entity.setCleanedUp(this.isCleanedUp());
		return entity;
	}
}
