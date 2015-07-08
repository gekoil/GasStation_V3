package BL;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientsSocketInfo {
	private Socket socket;
	private ObjectInputStream inputStream; 
	private ObjectOutputStream outputStream;
	private String clientAddress;
	
	public ClientsSocketInfo(Socket socket) {
		this.socket = socket;
		try {
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		clientAddress = socket.getInetAddress() + ":" + socket.getPort();
	}

	public Socket getSocket() {
		return socket;
	}
	
	public ObjectInputStream getInputStream() {
		return inputStream;
	}
	
	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
	public String getClientAddress() {
		return clientAddress;
	}
}
