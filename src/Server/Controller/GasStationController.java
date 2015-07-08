package Controller;

import Annotations.DuringWash;
import BL.Car;
import BL.ClientCar;
import BL.ClientsSocketInfo;
import BL.GasStation;
import DAL.ConnectorDAL;
import DAL.IDAL;
import DAL.Transaction;
import Listeners.*;
import Views.CarCreatorAbstractView;
import Views.MainFuelAbstractView;
import Views.StatisticsAbstractView;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.*;

public class GasStationController implements MainFuelEventListener,
		UIFuelEventListener, StatisticEventListener, UIStatisticsListener,
		UICarCreatorListener, CarsEventListener {

	private static int carId_generator = 9000;
	private static int SERVER_PORT = 9090;

	private boolean serverRunning = true;
	private GasStation gs;
	private IDAL dbConnector;
	private MainFuelAbstractView fuelView;
	private StatisticsAbstractView statisticView;
	private CarCreatorAbstractView carView;
	private ServerSocket listener;

	private HashMap<String, ClientsSocketInfo> clients;

	public GasStationController(GasStation gs, MainFuelAbstractView FuelView,
								StatisticsAbstractView statisticView, CarCreatorAbstractView carView) {
		this.gs = gs;
		this.fuelView = FuelView;
		this.statisticView = statisticView;
		this.carView = carView;
		this.gs.addFuelPoolListener(this);
		this.gs.addStatisticsListener(this);
		this.gs.addCarEventListener(this);
		this.fuelView.registerListener(this);
		this.statisticView.registerListener(this);
		this.carView.registerListener(this);

		dbConnector = ConnectorDAL.getInstance();
		dbConnector.checkGasStation(gs);

		clients = new HashMap<>();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				initServer();
			}
		};
		new Thread(runnable).start();
	}

	private void initServer() {
		try {
			listener = new ServerSocket(SERVER_PORT);
			listener.setSoTimeout(10000);
			while (serverRunning) {
				try {
					final Socket client = listener.accept();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								ClientsSocketInfo clientData = new ClientsSocketInfo(client);
								clients.put(clientData.getClientAddress(), clientData);
								Object carInput;
								do {
									carInput = clientData.getInputStream().readObject();
									if(carInput instanceof ClientCar) {
										// transform to server side Car object
										ClientCar car = (ClientCar) carInput;
										Car result = createNewCar(car.getFuel(),car.isNeedWash(), car.getPump(), clientData);

										// respond with client car with ID
										if(result == null)
											clientData.getOutputStream().writeObject(null);
									}
								} while(carInput != null);
							} catch (IOException | ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}).start();
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
			listener.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public GasStation getGs() {
		return gs;
	}

	public void setGs(GasStation gs) {
		this.gs = gs;
	}

	public MainFuelAbstractView getFuelview() {
		return fuelView;
	}

	public void setFuelview(MainFuelAbstractView fuelview) {
		this.fuelView = fuelview;
	}

	public StatisticsAbstractView getStatisticView() {
		return statisticView;
	}

	public void setStatisticView(StatisticsAbstractView statisticView) {
		this.statisticView = statisticView;
	}

	@Override
	public void theMainFuelIsLow(int liters) {
		if (!gs.isFillingMainFuelPool())
			fuelView.updateTheMainFuelIsLow(liters);
	}

	@Override
	public void finishedFillTheMainFuel(int liters) {
		fuelView.updateFinishedFillingMainFuel(liters);
	}

	@Override
	public void fireFillingTheMainFuel() {
		fuelView.updateFillingTheMainFuel();
	}

	@Override
	public void fireTheMainFuelIsFull() {
		fuelView.updateTheMainFuelIsFull();

	}

	@Override
	public void refill() {
		gs.fireFillUPMainFuelPoolEvent();
	}

	@Override
	public void fireTheCurrentCapacity(int liters) {
		fuelView.updateMainFuelCapacities(liters);
	}

	@Override
	public void getStatistics() {
		statisticView.setStatistics(gs.getStatistics().toString());
	}

	@Override
	public void ShowStatistics(String info) {
		statisticView.setStatistics(info);
	}

	@Override
	public void getCurrentCapacity() {
		fuelView.updateMainFuelCapacities(gs.getMfpool().getCurrentCapacity());

	}

	@Override
	public Car createNewCar(int liters, boolean wash, int pump, @Nullable ClientsSocketInfo owner) {
		if(liters > gs.getMfpool().getMaxCapacity()) {
			carView.updateErrorMessege("The amount fuel requested is to high!");
			return null;
		}

		if(liters < 0)
			liters = 0;

		Car c = new Car(carId_generator++, wash, liters, pump, gs);
		if(owner != null) {
			c.setOwner(owner);
			try {
				owner.getOutputStream().writeObject(c.toClientCar());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			gs.enterGasStation(c);
			carView.updateConfirmMessege("You Successfully create new car.\n" + "ID: " + c.getID());
			return c;
		} catch (Exception e) {
			carView.updateErrorMessege(e.getMessage());
			return null;
		}
	}

	@Override
	public void closeGasStation() {
		gs.closeGasStation();
		if(gs.isGasStationClosing()) {
			statisticView.setDisable();
			fuelView.setDisable();
			carView.setDisable();
			serverRunning = false;
		}
	}

	@Override
	public void getFueled(Car c, Transaction t) {
		dbConnector.storeTransaction(t);
		if(c.getOwner() != null) {
			ClientsSocketInfo carSocket = c.getOwner();
			if(!carSocket.getSocket().isClosed()) {
				try {
					ClientCar clCar = c.toClientCar();
					clCar.setStatus("Fueled");
					carSocket.getOutputStream().writeObject(clCar);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void getWashed(Car c, Transaction t) {
		if(t != null)
			dbConnector.storeTransaction(t);
		if(c.getOwner() != null) {
			ClientsSocketInfo carSocket = c.getOwner();
			if(!carSocket.getSocket().isClosed()) {
				try {
					ClientCar clCar = c.toClientCar();
					if(t == null) {
						List<Method> methods = getMethodsAnnotatedWith(Car.class, DuringWash.class);
						int index = (int) ((Math.random() * 10) % methods.size());
						clCar.setStatus(methods.get(index).getName());
					} else
						clCar.setStatus("Fueled");
					carSocket.getOutputStream().writeObject(clCar);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
		final List<Method> methods = new ArrayList<>();
		final List<Method> allMethods = new ArrayList<>(Arrays.asList(type.getDeclaredMethods()));
		for (final Method method : allMethods) {
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods;
	}

	@Override
	public Vector<Transaction> getHistory(LocalDateTime firstDate, LocalDateTime lastDate, int option) {
		Vector<Transaction> trans = dbConnector.getTransactions(firstDate, lastDate, option);
		return trans;
	}

}
