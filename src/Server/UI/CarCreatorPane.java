package UI;

import Listeners.UICarCreatorListener;
import Views.CarCreatorAbstractView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class CarCreatorPane extends GridPane implements CarCreatorAbstractView {
	
	private Label fuelLbl;
	private Label washLbl;
	private Label pumpLbl;
	private Text message;
	private TextField fuelField;
	private CheckBox washCbx;
	private ComboBox<Integer> pumpCmb;
	private Button submitBtn;
	private int choose = 0;
	private UICarCreatorListener listener;
	
	public CarCreatorPane(int pumps) {
		setPadding(new Insets(30, 30, 30, 30));
		setHgap(10);
		setVgap(10);
		
		fuelLbl = new Label("Fuel Amount:");
		add(fuelLbl, 0, 1);
		fuelField = new TextField("Liters");
		add(fuelField, 1, 1);
		
		washLbl = new Label("Need Wash:");
		add(washLbl, 0, 2);
		washCbx = new CheckBox();
		add(washCbx, 1, 2);
		
		pumpLbl = new Label("Pump Number:");
		createPumpCmb(pumps);
		
		add(pumpLbl, 0, 3);
		add(pumpCmb, 1, 3);
		
		createSubmitBtn();
		add(submitBtn, 0, 4);
		
		message = new Text("");
		add(message, 0, 0 , 10, 25);
		setVisible(true);
	}
	
	@Override
	public void registerListener(UICarCreatorListener lis) {
		this.listener = lis;
	}
	
	private void setMessage(String text, String id) {
		message.setText(text);
		message.setId(id);
		message.setVisible(true);
	}
	
	private void createPumpCmb(int pumps) {
		pumpCmb = new ComboBox<Integer>();
		for(int i = 1; i <= pumps; i++)
			pumpCmb.getItems().add(i);
		pumpCmb.setValue(1);
		pumpCmb.setTooltip(new Tooltip("Choose The Pump Number."));
		pumpCmb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				choose = pumpCmb.getSelectionModel().getSelectedItem()-1;
			}
		});
	}
	
	private void createSubmitBtn() {
		submitBtn = new Button("Submit");
		submitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				int fuel = 0;
				boolean wash = false;
				try {
					fuel = Integer.parseInt(fuelField.getText());
					wash = washCbx.selectedProperty().getValue();
					listener.createNewCar(fuel, wash, choose, null);
					fuelField.setText("Liters");
					washCbx.selectedProperty().set(false);
					pumpCmb.setValue(1);
				}catch (NumberFormatException e) {
					setMessage("The fuel amount is invalid!", "errorText");
				}
			}
		});
	}

	@Override
	public void updateErrorMessege(String Messege) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	setMessage(Messege, "errorText");
            }
        });
	}
	
	@Override
	public void updateConfirmMessege(String Messege) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	setMessage(Messege, "confirmText");
            }
        });
	}

	@Override
	public void setDisable() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	setDisable(true);
            }
        });
	}
	
}
