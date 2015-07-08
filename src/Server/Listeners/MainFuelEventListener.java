package Listeners;

public interface MainFuelEventListener {
	void theMainFuelIsLow(int liters);
	void finishedFillTheMainFuel(int liters);
	void fireFillingTheMainFuel();
	void fireTheMainFuelIsFull();
	void fireTheCurrentCapacity(int liters);
}
