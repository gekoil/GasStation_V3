package Listeners;

import DAL.Transaction;

import java.time.LocalDateTime;
import java.util.Vector;

public interface UIStatisticsListener {
	void getStatistics();
	void closeGasStation();
	Vector<Transaction> getHistory(LocalDateTime firstDate, LocalDateTime lastDate, int option);
}
