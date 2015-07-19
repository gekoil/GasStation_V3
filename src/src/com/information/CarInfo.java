package com.information;

import Controller.GasStationController;

public class CarInfo {
	
	private GasStationController gs;
	
	public CarInfo(GasStationController gs) {
		this.gs = gs;
	}
	
	public String getCarFee(int carId) {
		return gs.getCarFee(carId);
	}

}
