

import Client.Client;
import Controller.ClientController;
import View.CarRegisterUI;
import View.CarsTablePanel;
import View.ConnectionView;

import javax.swing.*;
import java.awt.*;

public class MainFrame {
	
	private static CarRegisterUI register;
	private static JTabbedPane tabedPane;
	private static ConnectionView connect;
	private static CarsTablePanel table;
	private static ClientController controller;

	private static JTabbedPane createContentPane() {
		tabedPane = new JTabbedPane();
		tabedPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		register = new CarRegisterUI();
		connect = new ConnectionView();
		BorderLayout border = new BorderLayout(10, 10);
		JPanel panel = new JPanel(border);
		panel.add(connect, BorderLayout.NORTH);
		panel.add(register, BorderLayout.CENTER);
		tabedPane.add("Register Car & Connection", panel);

		table = new CarsTablePanel();
		tabedPane.add("Cars Table", table);

		return tabedPane;
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Client Program");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(30, 30);
		frame.setLocation((int) Toolkit.getDefaultToolkit()
				.getScreenResolution() * 3, (int) Toolkit.getDefaultToolkit()
				.getScreenResolution() * 2);
		frame.setContentPane(createContentPane());
		controller = new ClientController(new Client(), table, register, connect);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
}
