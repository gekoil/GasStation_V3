package View;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Listeners.ConnectionUIListener;

public class ConnectionView extends JPanel implements AbstractConnectionView {
	
	private JButton connectionBtn;
	private JLabel statusLbl;
	private boolean correntStatus = false;
	private final String CONNECT_BTN = "Connect";
	private final String DISCONNECT_BTN = "Disconnect";
	private final String CONNECT_LBL = "The server connect";
	private final String DISCONNECT_LBL = "The server disconnect";
	private LinkedList<ConnectionUIListener> listeners;
	
	public ConnectionView() {
		listeners = new LinkedList<ConnectionUIListener>();
		FlowLayout flow = new FlowLayout();
		flow.setHgap(10);
		flow.setVgap(10);
		setLayout(flow);
		
		connectionBtn = new JButton(CONNECT_BTN);
		connectionBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(ConnectionUIListener l : listeners) {
					l.setConnection(!correntStatus);
				}
			}
		});
		
		statusLbl = new JLabel(DISCONNECT_LBL);
		
		add(connectionBtn);
		add(statusLbl);
	}
	
	private void setStatus() {
		if(correntStatus) {
			connectionBtn.setText(DISCONNECT_BTN);
			statusLbl.setText(CONNECT_LBL);
		}
		else {
			connectionBtn.setText(CONNECT_BTN);
			statusLbl.setText(DISCONNECT_LBL);
		}
	}

	@Override
	public void setConnectionStatus(boolean status) {
		correntStatus = status;
		setStatus();
	}

	@Override
	public void registerListener(ConnectionUIListener lis) {
		listeners.add(lis);
	}
	
}
