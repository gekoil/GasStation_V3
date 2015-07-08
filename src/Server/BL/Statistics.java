package BL;

public class Statistics {
	private int numOfCarsFueledUp;
	private int numOfCarsCleaned;
	private double fuelProfit;
	private double cleanProfit;

	public Statistics() {
		numOfCarsFueledUp = 0;
		numOfCarsCleaned = 0;
		fuelProfit = 0;
		cleanProfit = 0;
	}

	public int getNumOfCarsFueledUp() {
		return numOfCarsFueledUp;
	}

	public void setNumOfCarsFueledUp(int numOfCarsFueledUp) {
		this.numOfCarsFueledUp = numOfCarsFueledUp;
	}

	public int getNumOfCarsCleaned() {
		return numOfCarsCleaned;
	}

	public void setNumOfCarsCleaned(int numOfCarsCleaned) {
		this.numOfCarsCleaned = numOfCarsCleaned;
	}

	public double getFuelProfit() {
		return fuelProfit;
	}

	public void setFuelProfit(double fuelProfit) {
		this.fuelProfit = fuelProfit;
	}

	public double getCleanProfit() {
		return cleanProfit;
	}

	public void setCleanProfit(double cleanProfit) {
		this.cleanProfit = cleanProfit;
	}

	private String getFuelProfitFormated() {
		return String.format("%.2f", fuelProfit);
	} // setFuelProfitFormat

	@Override
	public String toString() {
		return "Statistics:\nAmount of cars fueled up: " + numOfCarsFueledUp
				+ "" + "\nAmount of cars cleaned: " + numOfCarsCleaned + " ."
				+ "\nFuel Profit: " + getFuelProfitFormated() + " ."
				+ "\nClean Profit: " + cleanProfit + " .";
	}
}
