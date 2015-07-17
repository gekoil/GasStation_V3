package View;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Listeners.RegisterUIListener;

public class CarRegisterUI extends JPanel implements AbstractRegisterView {

	private JLabel fuelLbl;
	private JLabel washLbl;
	private JLabel pumpLbl;
	private JTextField fuelFld;
	private JCheckBox washCbx;
	private JComboBox<Integer> pumpCmb;
	private JButton submitBtn;
	private int pumpNo = 1;
	private LinkedList<RegisterUIListener> listeners;

	public CarRegisterUI(int pumpsNum) {
		listeners = new LinkedList<RegisterUIListener>();
		createComponents(pumpsNum);
		GridLayout grid = new GridLayout(0, 2, 10, 10);
		JPanel panel = new JPanel(grid);
		panel.add(fuelLbl);
		panel.add(fuelFld);
		panel.add(washLbl);
		panel.add(washCbx);
		panel.add(pumpLbl);
		panel.add(pumpCmb);
		panel.add(submitBtn);
		panel.setPreferredSize(new Dimension(200, 100));
		add(panel);
	}

	private void createComponents(int pumpsNum) {
		fuelLbl = new JLabel("Fuel Amount:");
		washLbl = new JLabel("Need wash:");
		pumpLbl = new JLabel("Pump Number:");
		fuelFld = new JTextField(4);
		fuelFld.setText("Liters");
		washCbx = new JCheckBox();
		setPumpsNumber(0);
		submitBtn = new JButton("Submit");
		submitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int fuel = Integer.parseInt(fuelFld.getText());
					boolean wash = washCbx.isSelected();
					for(RegisterUIListener lis : listeners)
						lis.fireNewCar(fuel, wash, pumpNo);
					fuelFld.setText("Liters");
					washCbx.setSelected(false);
					pumpCmb.validate();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void registeListener(RegisterUIListener lis) {
		listeners.add(lis);
	}

	@Override
	public void setPumpsNumber(int pumps) {
		Integer[] pump = new Integer[pumps];
		for (int i = 0; i < pumps; i++)
			pump[i] = i + 1;
		pumpCmb = new JComboBox<Integer>(pump);
		pumpCmb.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pumpNo = (int) ((JComboBox<Integer>) arg0.getSource()).getSelectedItem();
			}
		});
		
	}

}
