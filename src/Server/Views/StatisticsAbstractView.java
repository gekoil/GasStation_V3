package Views;

import Listeners.UIStatisticsListener;

public interface StatisticsAbstractView {
	void registerListener(UIStatisticsListener lis);
	void setStatistics(String info);
	void setDisable();
}
