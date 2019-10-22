package NET;

import java.io.*;
import java.net.*;

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	private boolean running = true;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() { // an echo server
		try {
			while (running) {
				String data = in.readUTF(); // read a line of data from the stream
				//System.out.println(data);
				String [] parsedData = data.split("&");
				if(parsedData[0].equals(SharedTag.UPDATE_MAP_KEY)) {
					processMapUpdate(parsedData);
				}

			}
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
			try {
				this.running = false;
				clientSocket.close();
			} catch (IOException e1) {

			}
		}
	}

	private void processMapUpdate(String [] data) {
		try {
			if (data.length == 4) {
				boolean updated = GameModel.updateMap(Integer.valueOf(data[1]), Integer.valueOf(data[2]), Integer.valueOf(data[3]));
				if (updated) {
					out.writeUTF(SharedTag.MODEL_UPDATE + " " + GameModel.getValue(Integer.valueOf(data[1]), Integer.valueOf(data[2])));
				}
			}
		} catch (IOException e) {
			System.out.println("Error while map update: " + e.getMessage());
		}
	}
}
