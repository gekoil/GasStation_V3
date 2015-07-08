package Controller;

import BL.ClientCar;
import Client.Client;
import Listeners.ClientListener;
import Listeners.ConnectionUIListener;
import Listeners.RegisterUIListener;
import View.AbstractCarsView;
import View.AbstractConnectionView;
import View.AbstractRegisterView;

public class ClientController implements ClientListener, RegisterUIListener,
		ConnectionUIListener {
	private Client client;

	private AbstractCarsView carView;
	private AbstractRegisterView newCarView;
	private AbstractConnectionView connectView;

	public ClientController(Client client, AbstractCarsView carView,
			AbstractRegisterView newCarView, AbstractConnectionView connectView) {
		this.carView = carView;
		this.newCarView = newCarView;
		this.newCarView.registeListener(this);
		this.connectView = connectView;
		this.connectView.registerListener(this);
		this.client = client;
		this.client.registerListener(this);
	}

	@Override
	public void fireNewCar(int fuel, boolean needWash, int pump) {
		ClientCar car = new ClientCar(fuel, needWash, pump);
		client.sendCar(car);
	}

	@Override
	public void updateCarInfo(ClientCar car) {
		carView.carUpdate(car);
	}

	@Override
	public void updateConectionStatus(boolean status) {
		connectView.setConnectionStatus(status);
	}

	@Override
	public void setConnection(boolean onOff) {
		if(!onOff) {
			client.endOfConection();
		}
		else {
			client.start();
		}
	}

	@Override
	public void fireIlligalObject() {
	}

}
