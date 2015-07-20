package BL;

import Controller.GasStationController;
import UI.CarCreatorPane;
import UI.FuelPane;
import UI.UIStatistics;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GasStationServer extends Application implements EventHandler<WindowEvent> {

    private GasStation gs;
    private static FuelPane fuelPane;
    private static CarCreatorPane carPane;
    private static UIStatistics stat;
    private static final String BUILD_DATA = "input.xml";

    public static void main(String[] args) {
        launch(args);
    } // main

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("../config/beansConfig.xml");
        CreateGsFromXML creator = new CreateGsFromXML(BUILD_DATA, context);
        gs = creator.CreateGasStation();
        primaryStage.setScene(createScene());
        primaryStage.setTitle("Gas Station");
        primaryStage.setOnCloseRequest(this);
        GasStationController gasCtrl = new GasStationController(gs, fuelPane, stat, carPane, context);//dal);

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
        carPane = new CarCreatorPane(gs.getPumps().size());//.length);

        border.setAlignment(headline, Pos.CENTER);
        border.setTop(headline);
        border.setBottom(fuelPane);
        border.setCenter(stat);
        border.setRight(carPane);
        return scene;
    }

    @Override
    public void handle(WindowEvent e) {
        if(!gs.isGasStationClosing()) {
            e.consume();
            stat.setStatistics("For exit, first close the Gas Station.");
        }
    }

}
