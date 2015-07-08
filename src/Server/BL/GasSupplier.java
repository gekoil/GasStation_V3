package BL;

import java.util.Observable;
import java.util.Observer;

// GasSupplier listens on "less than 20%" event and fills the MainPool on fire
public class GasSupplier implements Runnable, Observer{
	private GasStation gsToSupplyGas;
	
	public GasSupplier() {
	}

	public void run() {
		if (gsToSupplyGas != null) {
			int numOfLitersToFill = (int)(Math.random()*100 + 100);
			// gsToSupplyGas is a parameter, since we need to write to gs file (logger)
			gsToSupplyGas.getMfpool().fillMainFuelPool(gsToSupplyGas, numOfLitersToFill);
		}
	}

	public void update(Observable o, Object arg1) {
		gsToSupplyGas = (GasStation)o;
	    Thread t = new Thread(this);
        t.start();
	}
}
