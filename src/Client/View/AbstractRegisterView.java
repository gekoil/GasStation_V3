package View;

import Listeners.RegisterUIListener;

public interface AbstractRegisterView {
	void registeListener(RegisterUIListener lis);
	void setPumpsNumber(int pumps);
}
