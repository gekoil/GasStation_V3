package Listeners;

import BL.ClientCar;

public interface ClientListener {
	void updateCarInfo(ClientCar car);
	void fireIlligalObject();
	void updateConectionStatus(boolean status);
}
