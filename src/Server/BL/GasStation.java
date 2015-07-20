package BL;

import Annotations.Loggable;
import DAL.Entities.GasStationsEntity;
import DAL.ServiceType;
import DAL.Transaction;
import Listeners.CarsEventListener;
import Listeners.MainFuelEventListener;
import Listeners.StatisticEventListener;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

// GasStation is Observable since it fires the "less than 20%" event
// GasSupplier listens on the event and fills the MainFuelPool on fire
public class GasStation extends Observable {
	public  static final String LOGGER_NAME = "Gas_Station Logger";
	private static final Logger LOG = Logger.getLogger(LOGGER_NAME);
	private static int idCounter = 0;
	private int id;
	private FileHandler handler;
	private Vector<MainFuelEventListener> fuelListeners;
	private Vector<StatisticEventListener> statisticsListeners;
	private Vector<CarsEventListener> carsEventListeners;
	private int numOfPumps;
	private double pricePerLiter;
	private ArrayList<Pump> pumps;
	private MainFuelPool mfpool;
	private CleaningService cs;
	// this ServiceExecutor can accept any amount of Runnable objects
	//(not using FixedThreadPool since we don't know how many cars will enter the gasStationId)
	private ExecutorService gasStationQueue = Executors.newCachedThreadPool();
	private Statistics statistics = new Statistics();
	private GasSupplier supplier = new GasSupplier();
	// these flags are for synchronizing purposes
	private boolean isFillingMainFuelPool = false;
	private boolean gasStationClosing = false;
	private int numOfCarsFuelingUpCurrently;
	private int numOfCarsInTheGasStationCurrently;

	public GasStation() {
		id = idCounter++;
		this.fuelListeners = new Vector<>();
		this.statisticsListeners = new Vector<>();
		this.carsEventListeners = new Vector<>();
		try {
			this.handler = new FileHandler("Gas Station Log.txt");
			this.handler.setFormatter(new MyFormat());
			this.handler.setFilter(new MyObjectFilter(this));
			GasStation.getLog().addHandler(this.handler);
			GasStation.getLog().setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		// the GasSupplier is the observer which fills the MainPool up on less than 20% event
		this.addObserver(supplier);
		numOfCarsFuelingUpCurrently = 0;
		numOfCarsInTheGasStationCurrently = 0;
	}

	public GasStation(int numOfPumps, double pricePerLiter, MainFuelPool mfpool, CleaningService cs, ApplicationContext context) {
		id = idCounter++;
		this.fuelListeners = new Vector<>();
		this.statisticsListeners = new Vector<>();
		this.carsEventListeners = new Vector<>();
		this.numOfPumps = numOfPumps;
		this.pricePerLiter = pricePerLiter;
		this.pumps = new ArrayList<>();//Pump[numOfPumps];
		for (int i = 0; i < numOfPumps; i++) {
			//pumps[i] = new Pump((i+1), this);
			pumps.add(new Pump((i+1), this));
		}
		this.mfpool = mfpool;
		this.cs = cs;
		// the GasSupplier is the observer which fills the MainPool up on less than 20% event
		this.addObserver(supplier);
		numOfCarsFuelingUpCurrently = 0;
		numOfCarsInTheGasStationCurrently = 0;
		fireTheMainFuelPoolCapacity();
	}

	@Loggable(logMessage = "Car enter the station")
	public void enterGasStation(Car car) {
		gasStationQueue.execute(car);
		numOfCarsInTheGasStationCurrently++;
	}  // enterGasStation

	public void fuelUp(Car car) {
		synchronized (this) {
			// choosing the shortest waiting queue
			//pumps[car.getPumpNum() - 1].checkWhichQueueIsShorter(cs, car);
			boolean queueOnCleanServiceIsShorter = pumps.get(car.getPumpNum() - 1).checkWhichQueueIsShorter(cs, car);
			if (queueOnCleanServiceIsShorter)
				return;
			if (mfpool.getCurrentCapacity() <= 0 || car.getNumOfLiters() > mfpool.getCurrentCapacity()) {
				// if the GasStation has less fuel than the car needs, fire the filling event!
				if (!isFillingMainFuelPool()) {
					fireFillUPMainFuelPoolEvent();
				}
				// waiting until the MainFuelPool is filled up
				do {
					try {
						fireFillingTheMainFuel();
						pumps.get(car.getPumpNum() - 1).lock();//pumps[car.getPumpNum() - 1].lock();
						pumps.get(car.getPumpNum() - 1).getIsEligibleToFuelUp().await();//pumps[car.getPumpNum() - 1].getIsEligibleToFuelUp().await();
						pumps.get(car.getPumpNum() - 1).unlock();//pumps[car.getPumpNum() - 1].unlock();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while (isFillingMainFuelPool());
			}
			mfpool.setCurrentCapacity(mfpool.getCurrentCapacity() - car.getNumOfLiters());
		}
		// fueling up by the chosen pump
		Transaction trans = new Transaction();
		trans.gasStationId = getId();
		trans.carId = car.getID();
		trans.pump = car.getPumpNum();
		trans.amount = car.getNumOfLiters() * pricePerLiter;
		trans.timeStamp = LocalDateTime.now();
		trans.type = ServiceType.FUEL;

		pumps.get(car.getPumpNum() - 1).pumpFuelUp(car, mfpool);//pumps[car.getPumpNum()-1].pumpFuelUp(car, mfpool);

		statistics.setNumOfCarsFueledUp(statistics.getNumOfCarsFueledUp() + 1);
		statistics.setFuelProfit(statistics.getFuelProfit() + pricePerLiter * car.getNumOfLiters());
		fireCarFueledEvent(car, trans);
		// if less than 20% and isn't filling the fuel pool currently, raise an event
		if (mfpool.getCurrentCapacity() <= mfpool.getMaxCapacity()*0.2) {
			if (!isFillingMainFuelPool()) {
				fireFillUPMainFuelPoolEvent();
			}
		}
	}  // fuelUp

	@Loggable(logMessage = "filling the main fuel pool")
	public void fireFillUPMainFuelPoolEvent() {
		super.setChanged();
		super.notifyObservers(this);
	}  // fireFillUPMainFuelPoolEvent
	
	public void cleanCar(Car car) {
		// continueToManualClean can be false in case, when the GasStation is closing and
		// the car didn't pass the AutoClean! So the car shouldn't pass CleanService at all,
		// if it hasn't begun with the process
		boolean continueToManualClean = autoClean(car);
		if (continueToManualClean) {
			manualClean(car);
		}
		Transaction trans = new Transaction();
		trans.gasStationId = getId();
		trans.carId = car.getID();
		trans.amount = cs.getPrice();
		trans.timeStamp = LocalDateTime.now();
		trans.type = ServiceType.CLEANING;
		fireCarWashedEvent(car, trans);
	} // cleanCar

	public boolean autoClean(Car car) {
		boolean continueToManualClean;
		continueToManualClean = cs.getAutoClean().autoClean(gasStationClosing, car, cs.getSecondsPerAutoClean(), cs, this);
		fireCarWashedEvent(car, null);
		return continueToManualClean;
	} // autoClean

	public void manualClean(Car car) {
		// enter the manual-cleaning process and lock the object
		int num_of_team_to_occupy = 0;
		while (true) {
			if (!cs.getManualClean()[num_of_team_to_occupy].isLocked()) {
				cs.getManualClean()[num_of_team_to_occupy].manualClean(car, cs, this);
				car.setCleanedUp(true);
				break;
			}
			num_of_team_to_occupy++;
			if (num_of_team_to_occupy == cs.getNumOfTeams()) {
				num_of_team_to_occupy = 0;
			}
		}  // while-loop
		statistics.setNumOfCarsCleaned(statistics.getNumOfCarsCleaned() + 1);
		statistics.setCleanProfit(statistics.getCleanProfit() + cs.getPrice());
	} // manualClean

	public void closeGasStation() {
		// can't close the gas station while filling up the main fuel pool
		if (isFillingMainFuelPool) {
			fireStatistics("can't close the gas station while filling up the main fuel pool");
			return;
		}
		gasStationClosing = true;
		// wait until all threads in the queue run until the end(only in case of fuelingUp)
		gasStationQueue.shutdown();
		if (numOfCarsInTheGasStationCurrently > 0)
			fireStatistics("The statistics will be shown after the last car leaves the gas station");
		else {
			fireStatistics(statistics.toString());
		}
	}  // closeGasStation

	public int getId() {
		return id;
	}

	public GasSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(GasSupplier supplier) {
		this.supplier = supplier;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	public boolean isGasStationClosing() {
		return gasStationClosing;
	}

	public void setGasStationClosing(boolean gasStationClosing) {
		this.gasStationClosing = gasStationClosing;
	}

	public ArrayList<Pump> getPumps() {
		return pumps;
	}

	public void setPumps(ArrayList<Pump> pumps) {
		this.pumps = pumps;
	}

	public CleaningService getCs() {
		return cs;
	}

	public void setCs(CleaningService cs) {
		this.cs = cs;
	}

	public static Logger getLog() {
		return LOG;
	}

	public int getNumOfCarsFuelingUpCurrently() {
		return numOfCarsFuelingUpCurrently;
	}

	public void setNumOfCarsFuelingUpCurrently(int numOfCarsFuelingUpCurrently) {
		this.numOfCarsFuelingUpCurrently = numOfCarsFuelingUpCurrently;
	}

	public boolean isFillingMainFuelPool() {
		return isFillingMainFuelPool;
	}

	public void setFillingMainFuelPool(boolean isFillingMainFuelPool) {
		this.isFillingMainFuelPool = isFillingMainFuelPool;
	}

	public MainFuelPool getMfpool() {
		return mfpool;
	}
	
	
	public void setMfpool(MainFuelPool mfpool) {
		this.mfpool = mfpool;
		fireTheMainFuelPoolCapacity();
	}

	public int getNumOfCarsInTheGasStationCurrently() {
		return numOfCarsInTheGasStationCurrently;
	}

	public void setNumOfCarsInTheGasStationCurrently(
			int numOfCarsInTheGasStationCurrently) {
		this.numOfCarsInTheGasStationCurrently = numOfCarsInTheGasStationCurrently;
	}

	public double getPricePerLiter() {
		return pricePerLiter;
	}

	public void setPricePerLiter(double pricePerLiter) {
		this.pricePerLiter = pricePerLiter;
	}

	@Override
	public String toString() {
		return "GasStation [numOfPumps=" + numOfPumps + ", pricePerLiter="
				+ pricePerLiter + ", mfpool=" + mfpool + ", cs=" + cs + "]";
	}

	public Vector<MainFuelEventListener> getFuelListeners() {
		return fuelListeners;
	}

	public void setFuelListeners(Vector<MainFuelEventListener> fuelListeners) {
		this.fuelListeners = fuelListeners;
	}

	public Vector<StatisticEventListener> getStatisticsListeners() {
		return statisticsListeners;
	}

	public void setStatisticsListeners(
			Vector<StatisticEventListener> statisticsListeners) {
		this.statisticsListeners = statisticsListeners;
	}

	public Vector<CarsEventListener> getCarsEventListeners() {
		return carsEventListeners;
	}

	public void setCarsEventListeners(Vector<CarsEventListener> carsEventListeners) {
		this.carsEventListeners = carsEventListeners;
	}

	public void addFuelPoolListener(MainFuelEventListener lis) {
		fuelListeners.add(lis);
	}

	public void addStatisticsListener(StatisticEventListener lis) {
		statisticsListeners.add(lis);
	}

	public void addCarEventListener(CarsEventListener lis) {
		carsEventListeners.add(lis);
	}

	protected void fireFillUPMainFuelEvent() {
		for(MainFuelEventListener l : fuelListeners)
			l.theMainFuelIsLow(mfpool.getCurrentCapacity());
	}
	
	@Loggable(logMessage = "The fuel pool get filled")
	protected void finishedFillTheMainFuel() {
		for(MainFuelEventListener l : fuelListeners)
			l.finishedFillTheMainFuel(mfpool.getCurrentCapacity());
	}
	
	@Loggable(logMessage = "Car get washed")
	protected void fireFillingTheMainFuel() {
		for(MainFuelEventListener l : fuelListeners)
			l.fireFillingTheMainFuel();
	}
	
	@Loggable(logMessage = "Main fuel pool is full")
	protected void fireTheMainFuelIsFull() {
		for(MainFuelEventListener l : fuelListeners)
			l.fireTheMainFuelIsFull();
	}

	protected void fireTheMainFuelPoolCapacity() {
		for(MainFuelEventListener l : fuelListeners)
			l.fireTheCurrentCapacity(mfpool.getCurrentCapacity());
	}

	protected void updateStatistics() {
		for(StatisticEventListener l : statisticsListeners)
			l.ShowStatistics(statistics.toString());
	}
	
	@Loggable(logMessage = "Car get cleand")
	protected void fireCarWashedEvent(Car car, Transaction t) {
		for(CarsEventListener l : carsEventListeners)
			l.getWashed(car, t);
	}
	
	@Loggable(logMessage = "Car get fueld")
	protected void fireCarFueledEvent(Car car, Transaction t) {
		for(CarsEventListener l : carsEventListeners)
			l.getFueled(car, t);
	}
	
	protected void fireStatistics(String stat) {
		for(StatisticEventListener l : statisticsListeners)
			l.ShowStatistics(stat);
	}

	public GasStationsEntity toEntity() {
		GasStationsEntity entity = new GasStationsEntity();
		//entity.setGasRevenue(this.get);
		entity.setId(this.getId());
		entity.setGasRevenue(this.statistics.getFuelProfit());
		entity.setCleanRevenue(this.statistics.getCleanProfit());
		entity.setCarsFueled(this.statistics.getNumOfCarsFueledUp());
		entity.setCarsCleaned(this.statistics.getNumOfCarsCleaned());
		return entity;
	}

	public void setNumOfPumps(int numOfPumps) {
		this.numOfPumps = numOfPumps;
	}
	
	@Loggable(logMessage = "Pump added")
	public void addPump(Pump pump) {
		if(pumps == null) {
			pumps = new ArrayList<>(pump.getNum());
		}
		if (pumps.size() > pump.getNum()) {
			pumps.set(pump.getNum(), pump);
		} else {
			pumps.add(pump.getNum(), pump);
		}
		pump.setStation(this);
	}
}  // GasStation
