package BL;

import DAL.IDAL;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;


public class CreateGsFromXML {

	private ApplicationContext context;
	private final String FILE_NAME;
	
	public CreateGsFromXML(String fileName, ApplicationContext context) {
		FILE_NAME = fileName;
		this.context = context;
	}
	
	public GasStation CreateGasStation() {
		// XML DOM-parsed values
		GasStation gs = null;
		try {
			Scanner in = new Scanner(System.in);
			// reading data from the XML file
			File inputFile = new File(FILE_NAME);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList root = doc.getChildNodes();
			gs = getGasStationXML(root);
			Node gasStationNode = getNode("GasStation", root);
			Car[] cars = getCarsXML(gasStationNode, gs);

			// inserting the cars(threads) into the gas station
			if(cars != null && cars.length > 0) {
				IDAL dal = ((IDAL)context.getBean("iDAL")).getInstance();
				for (Car car : cars) {
					dal.addCar(car);
					gs.enterGasStation(car);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return gs;
	}
	
	// these functions are for XML data parsing
		private static Node getNode(String tagName, NodeList nodes) {
		    for (int i = 0; i < nodes.getLength(); i++) {
		        Node node = nodes.item(i);
		        if (node.getNodeName().equalsIgnoreCase(tagName)) {
		            return node;
		        }
		    }
		    return null;
		}  // getNode
		
		private static String getNodeAttr(String attrName, Node node) {
		    NamedNodeMap attrMap = node.getAttributes();
		    for (int i = 0; i < attrMap.getLength(); i++ ) {
		        Node attr = attrMap.item(i);
		        if (attr.getNodeName().equalsIgnoreCase(attrName)) {
		            return attr.getNodeValue();
		        }
		    }
		    return "";
		}  // getNodeAttr
		
		private GasStation getGasStationXML(NodeList root) {
			// getting the GasStation Node
			Node gasStationNode = getNode("GasStation", root);
			String numOfPumpsString = getNodeAttr("numOfPumps", gasStationNode);
			int numOfPumps = Integer.parseInt(numOfPumpsString);
			String pricePerLiterString = getNodeAttr("pricePerLiter",
					gasStationNode);
			double pricePerLiter = Double.parseDouble(pricePerLiterString);

			// getting the MainFuelPool Node
			Node mainFuelPoolNode = getNode("MainFuelPool",gasStationNode.getChildNodes());
			String maxCapacityString = getNodeAttr("maxCapacity", mainFuelPoolNode);
			int maxCapacity = Integer.parseInt(maxCapacityString);
			String currentCapacityString = getNodeAttr("currentCapacity",
					mainFuelPoolNode);
			int currentCapacity = Integer.parseInt(currentCapacityString);

			// getting the CleaningService Node
			Node cleaningServiceNode = getNode("CleaningService",gasStationNode.getChildNodes());
			String numOfTeamsString = getNodeAttr("numOfTeams", cleaningServiceNode);
			int numOfTeams = Integer.parseInt(numOfTeamsString);
			String priceString = getNodeAttr("price", cleaningServiceNode);
			int price = Integer.parseInt(priceString);

			String secondsPerAutoCleanString = getNodeAttr("secondsPerAutoClean", cleaningServiceNode);
			int secondsPerAutoClean = Integer.parseInt(secondsPerAutoCleanString);

			// TODO: add call to context and get bean
			MainFuelPool pool = new MainFuelPool(maxCapacity, currentCapacity);
			// TODO: add call to context and get bean
			CleaningService cs = new CleaningService(numOfTeams ,price, secondsPerAutoClean);
			// TODO: add call to context and get bean
			GasStation gs = new GasStation(numOfPumps, pricePerLiter, pool, cs);
			return gs;
		}  // getGasStationXML
		
		private Car[] getCarsXML(Node gasStationNode, GasStation gs) {
			Car[] cars = null;
			// getting the Cars Node
			Node carsNode = getNode("Cars", gasStationNode.getChildNodes());
			Element el = (Element) carsNode;
			// getting the Cars List
			NodeList carsList = null;
			if (el != null) {
				carsList = el.getElementsByTagName("Car");
				cars = new Car[carsList.getLength()];
				for (int i = 0; i < carsList.getLength(); i++) {
					// getting the Car node
					Node carNode = carsList.item(i);
					String idString = getNodeAttr("id", carNode);
					int id = Integer.parseInt(idString);

					String wantCleaningString = getNodeAttr("wantCleaning", carNode);
					boolean wantCleaning = Boolean.parseBoolean(wantCleaningString);

					// getting the wantsFuel Node
					Node wantsFuelNode = getNode("WantsFuel", carNode.getChildNodes());
					if (wantsFuelNode != null) {
						String numOfLitersString = getNodeAttr("numOfLiters", wantsFuelNode);
						int numOfLiters = Integer.parseInt(numOfLitersString);
						String pumpNumString = getNodeAttr("pumpNum", wantsFuelNode);
						int pumpNum = Integer.parseInt(pumpNumString);

						Car car = (Car)context.getBean("car");
						car.setId(id);
						car.setWantCleaning(wantCleaning);
						car.setNumOfLiters(numOfLiters);
						car.setPumpNum(pumpNum);
						car.setGasStation(gs);
						cars[i] = car;//new Car(id, wantCleaning, numOfLiters, pumpNum, gs);
					} else {
						// TODO: add call to context and get bean
						Car car = (Car)context.getBean("car");
						car.setId(id);
						car.setWantCleaning(wantCleaning);
						car.setNumOfLiters(0);
						car.setPumpNum(-1);
						car.setGasStation(gs);
						cars[i] = car;//new Car(id, wantCleaning, 0, -1, gs);
					}
				}  // for-loop
			}
			return cars;
		} // getCarsXML
	
}
