package BL;

import java.io.File;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CreateGsFromXML {
	
	private GasStation gs;
	private final String FILE_NAME;
	
	public CreateGsFromXML(String fileName) {
		FILE_NAME = fileName;
	}
	
	public GasStation CreatGasStation() {
		// XML DOM-parsed values
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
					for (int i = 0; i < cars.length; i++) {
						gs.enterGasStation(cars[i]);
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
		    NamedNodeMap attrs = node.getAttributes();
		    for (int i = 0; i < attrs.getLength(); i++ ) {
		        Node attr = attrs.item(i);
		        if (attr.getNodeName().equalsIgnoreCase(attrName)) {
		            return attr.getNodeValue();
		        }
		    }
		    return "";
		}  // getNodeAttr
		
		private static GasStation getGasStationXML(NodeList root) {	
			// getting the GasStation Node
			Node gasStationNode = getNode("GasStation", root);
			String numOfPumpsString = getNodeAttr("numOfPumps", gasStationNode);
			int numOfPumps = Integer.parseInt(numOfPumpsString);
			//System.out.print("numOfPumps: " + numOfPumps + "   ");
			String pricePerLiterString = getNodeAttr("pricePerLiter",
					gasStationNode);
			double pricePerLiter = Double.parseDouble(pricePerLiterString);
			//System.out.println("pricePerLiter: " + pricePerLiter);

			// getting the MainFuelPool Node
			Node mainFuelPoolNode = getNode("MainFuelPool",gasStationNode.getChildNodes());
			String maxCapacityString = getNodeAttr("maxCapacity", mainFuelPoolNode);
			int maxCapacity = Integer.parseInt(maxCapacityString);
			//System.out.print("maxCapacity: " + maxCapacity + "   ");
			String currentCapacityString = getNodeAttr("currentCapacity",
					mainFuelPoolNode);
			int currentCapacity = Integer.parseInt(currentCapacityString);
			//System.out.println("currentCapacity: " + currentCapacity);

			// getting the CleaningService Node
			Node cleaningServiceNode = getNode("CleaningService",gasStationNode.getChildNodes());
			String numOfTeamsString = getNodeAttr("numOfTeams", cleaningServiceNode);
			int numOfTeams = Integer.parseInt(numOfTeamsString);
			//System.out.print("numOfTeams: " + numOfTeams + "   ");
			String priceString = getNodeAttr("price", cleaningServiceNode);
			int price = Integer.parseInt(priceString);
			//System.out.print("price: " + price + "   ");
			String secondsPerAutoCleanString = getNodeAttr("secondsPerAutoClean", cleaningServiceNode);
			int secondsPerAutoClean = Integer.parseInt(secondsPerAutoCleanString);
			//System.out.println("secondsPerAutoClean: " + secondsPerAutoClean);

			MainFuelPool pool = new MainFuelPool(maxCapacity, currentCapacity);
			CleaningService cs = new CleaningService(numOfTeams ,price, secondsPerAutoClean);
			GasStation gs = new GasStation(numOfPumps, pricePerLiter, pool, cs);
			return gs;
		}  // getGasStationXML
		
		private static Car[] getCarsXML(Node gasStationNode, GasStation gs) {
			Car[] cars = null;
			// getting the Cars Node
			Node carsNode = getNode("Cars", gasStationNode.getChildNodes());
			Element el = (Element) carsNode;
			// getting the Cars List
			//System.out.println("Cars List:");
			NodeList carsList = el.getElementsByTagName("Car");
			cars = new Car[carsList.getLength()];
			for (int i = 0; i < carsList.getLength(); i++) {
				// getting the Car node
				Node carNode = carsList.item(i);
				String idString = getNodeAttr("id", carNode);
				int id = Integer.parseInt(idString);
				//System.out.print("Car ID: " + id + "   ");
				String wantCleaningString = getNodeAttr("wantCleaning", carNode);
				boolean wantCleaning = Boolean.parseBoolean(wantCleaningString);
				//System.out.print("wantCleaning: " + wantCleaning + "   ");

				// getting the wantsFuel Node
				Node wantsFuelNode = getNode("WantsFuel", carNode.getChildNodes());
				if (wantsFuelNode != null) {
					String numOfLitersString = getNodeAttr("numOfLiters", wantsFuelNode);
					int numOfLiters = Integer.parseInt(numOfLitersString);
					//System.out.print("numOfLiters: " + numOfLiters + "   ");
					String pumpNumString = getNodeAttr("pumpNum", wantsFuelNode);
					int pumpNum = Integer.parseInt(pumpNumString);
					//System.out.println("pumpNum: " + pumpNum);
					cars[i] = new Car(id, wantCleaning, numOfLiters, pumpNum, gs);
				} else {
					//System.out.println();
					cars[i] = new Car(id, wantCleaning, 0, -1, gs);
				}
			}  // for-loop
			return cars;
		} // getCarsXML
	
}
