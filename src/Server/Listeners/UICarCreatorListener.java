package Listeners;

import BL.Car;
import BL.ClientsSocketInfo;

import com.sun.istack.internal.Nullable;

public interface UICarCreatorListener {
	Car createNewCar(int liters, boolean wash, int pump, @Nullable ClientsSocketInfo owner);
}
