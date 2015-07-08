package DAL;

import BL.GasStation;
import BL.Pump;

import java.time.LocalDateTime;
import java.util.Vector;

public class PersistenceDAL implements IDAL {

    private static IDAL instance;

    private PersistenceDAL() {

    }

    public static IDAL getInstance() {
        if (instance == null) {
            instance = new PersistenceDAL();
        }
        return instance;
    }

    @Override
    public boolean checkGasStation(GasStation gs) {
        return false;
    }

    @Override
    public boolean setPumps(Pump[] pumps, int gs) {
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
}
