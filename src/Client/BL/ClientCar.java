package BL;

import java.io.Serializable;

public class ClientCar implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private boolean needWash;
	private int fuel;
	private String status;
	private int pump;
	
	public ClientCar(int fuel, boolean wash, int pump) {
		this.fuel = fuel;
		this.needWash = wash;
		this.pump = pump;
		this.status = "";
	}
	
	public ClientCar(int id,int fuel, boolean wash, int pump) {
		this.id = id;
		this.fuel = fuel;
		this.needWash = wash;
		this.pump = pump;
		this.status = "";
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isNeedWash() {
		return needWash;
	}

	public void setNeedWash(boolean needWash) {
		this.needWash = needWash;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPump() {
		return pump;
	}

	public void setPump(int pump) {
		this.pump = pump;
	}
}
