package View;

import Listeners.ConnectionUIListener;

public interface AbstractConnectionView {
	void setConnectionStatus(boolean status);
	void registerListener(ConnectionUIListener lis);
}
