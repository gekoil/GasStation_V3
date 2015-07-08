package UI;

import Listeners.UIFuelEventListener;
import Views.MainFuelAbstractView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class FuelPane extends StackPane implements  MainFuelAbstractView {
	
	private Label headline;
	private Label fuelStatus;
	private Pane centerPane;
	private Button refillBtn;
	private Button capacityBtn;
	private UIFuelEventListener listener;
	
	public FuelPane() {
		headline = new Label("MAIN FUEL POOL");
		fuelStatus = new Label("");
		setTheCenter();
		BorderPane mainBorder = new BorderPane();
		mainBorder.setAlignment(headline, Pos.CENTER);
		mainBorder.setTop(headline);
		mainBorder.setCenter(centerPane);
		mainBorder.setBottom(fuelStatus);
		getChildren().add(mainBorder);
	}
	
	private void setTheCenter() {
		centerPane = new HBox();
		centerPane.setId("FuelControl");
		
		refillBtn = new Button("REFILL THE MAIN POOL");
		refillBtn.setDisable(true);
		refillBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				listener.refill();
			}
		});
		capacityBtn = new Button("Show Capacity");
		capacityBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				listener.getCurrentCapacity();
			}
		});
		
		centerPane.getChildren().addAll(capacityBtn, refillBtn);
	}
	
	public void registerListener(UIFuelEventListener lis) {
		listener = lis;
	}

	@Override
	public void updateTheMainFuelIsLow(int liters) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	fuelStatus.setText("Liters in the main pool: " + liters + " , please refill!");
            	refillBtn.setDisable(false);
            }
        });
	}

	@Override
	public void updateMainFuelCapacities(int liters) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	fuelStatus.setText("The Main fuel pool corrent capacity is " + liters + "");
            	refillBtn.setDisable(false);
            }
        });
		
	}

	@Override
	public void updateTheMainFuelIsFull() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	fuelStatus.setText("The Main Fuel Pool is full, therefore can't be filled up currently!");
            }
        });	
	}

	@Override
	public void updateFinishedFillingMainFuel(int liters) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	fuelStatus.setText("Finished filling up the Main Fuel Pool, current capacity is " + liters);
            	refillBtn.setDisable(false);
            	
            }
        });		
	}

	@Override
	public void updateFillingTheMainFuel() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	fuelStatus.setText("Filling up the Main Fuel Pool!");
            	refillBtn.setDisable(true);
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
