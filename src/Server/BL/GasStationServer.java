package BL;

import Controller.GasStationController;
import UI.CarCreatorPane;
import UI.FuelPane;
import UI.UIStatistics;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GasStationServer extends Application {

    private GasStation gs;
    private static FuelPane fuelPane;
    private static CarCreatorPane carPane;
    private static UIStatistics stat;
    GasStationController fuelCtrl;
    private static final String BUILD_DATA = "input.xml";

    public static void main(String[] args) {
        launch(args);
    } // main

    @Override
    public void start(Stage primaryStage) throws Exception {
        CreateGsFromXML creator = new CreateGsFromXML(BUILD_DATA);
        gs = creator.CreatGasStation();
        primaryStage.setScene(createScene());
        primaryStage.setTitle("Gas Station");

        fuelCtrl = new GasStationController(gs, fuelPane, stat, carPane);

        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }

    public Scene createScene() {
        BorderPane border = new BorderPane();
        Scene scene = new Scene(border);
        scene.getStylesheets().add(GasStationServer.class.getResource("/UI/Style.css").toExternalForm());

        fuelPane = new FuelPane();
        fuelPane.setId("mainFuelBox");
        stat = new UIStatistics();
        Text headline = new Text("WELCOME TO THE GAS STATION");
        headline.setId("headline");
        carPane = new CarCreatorPane(gs.getPumps().length);

        border.setAlignment(headline, Pos.CENTER);
        border.setTop(headline);
        border.setBottom(fuelPane);
        border.setCenter(stat);
        border.setRight(carPane);
        return scene;
    }
}
