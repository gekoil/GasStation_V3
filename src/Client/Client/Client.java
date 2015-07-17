package Client;

import BL.ClientCar;
import Listeners.ClientListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class Client extends Thread{
	private static int SERVER_PORT = 9090;

	private Socket socket = null;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private LinkedList<ClientListener> listeners;
	private boolean endOfConnection = false;

	public Client() {
		listeners = new LinkedList<ClientListener>();
	}

	public void run() {
		try {
			socket = new Socket("localhost", SERVER_PORT);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			for(ClientListener l : listeners)
				l.updateConectionStatus(true);
			Runnable runInput = new Runnable() {
				@Override
				public void run() {
					carReceiver();
				}
			};
			new Thread(runInput).start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void sendCar(ClientCar car) {
		try {
			outputStream.writeObject(car);
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void carReceiver() {
		while(!endOfConnection) {
			try {
				Object temp = inputStream.readObject();
				if (temp == null) {
					endOfConection();
				} else if(temp instanceof ClientCar)  {
					ClientCar car =(ClientCar) temp;
					for(ClientListener l : listeners)
						l.updateCarInfo(car);
				}
				else if(temp instanceof Integer) {
					int pump = (int) temp;
					for(ClientListener l : listeners)
						l.fireIlligalObject();
				}
			} catch (ClassNotFoundException | IOException e) {
				endOfConection();
			}
		}
	}

	public void registerListener(ClientListener lis) {
		listeners.add(lis);
	}

	public synchronized void endOfConection() {
		if(!endOfConnection) {
			sendCar(null);
			try {
				outputStream.close();
				inputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			endOfConnection = true;
			for(ClientListener l : listeners)
				l.updateConectionStatus(!endOfConnection);
		}
	}
}
