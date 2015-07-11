package DAL;

import BL.GasStation;
import BL.Pump;

import java.time.LocalDateTime;
import java.util.Vector;

/**
 * Created by Jack on 08/07/2015.
 */
public interface IDAL {
	boolean checkGasStation(GasStation gs);

	boolean setPumps(Pump[] pumps, GasStation gs);

	boolean storeTransaction(Transaction transaction);

	Vector<Transaction> getTransactions(LocalDateTime first,
										LocalDateTime last, int option);

	IDAL getInstance();
}
