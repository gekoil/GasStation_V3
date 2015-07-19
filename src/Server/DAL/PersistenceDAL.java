package DAL;

import BL.Car;
import BL.GasStation;
import BL.Pump;

import java.time.LocalDateTime;
import java.util.Vector;

public class PersistenceDAL implements IDAL {

    private static IDAL instance;

    private PersistenceDAL() {

    }

    public IDAL getInstance() {
        if (instance == null) {
            instance = new PersistenceDAL();
        }
        return instance;
    }

    @Override
    public boolean addCar(Car car) {
        return false;
    }

    @Override
    public boolean checkGasStation(GasStation gs) {
        return false;
    }

    @Override
    public boolean setPumps(Pump[] pumps, GasStation gs) {
        return false;
    }

    @Override
    public boolean storeTransaction(Transaction transaction) {
        return false;
    }

    @Override
    public Vector<Transaction> getTransactions(LocalDateTime first, LocalDateTime last, int option) {
        return null;
    }

	@Override
	public String getCarFee(int id) {
		return "";
	}
}
