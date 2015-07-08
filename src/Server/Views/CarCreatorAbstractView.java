package Views;
import Listeners.UICarCreatorListener;


public interface CarCreatorAbstractView {
	void registerListener(UICarCreatorListener lis);
	void updateErrorMessege(String Messege);
	void updateConfirmMessege(String Messege);
	void setDisable();
}
