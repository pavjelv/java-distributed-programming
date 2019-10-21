package NET;

import java.io.*;
import java.net.*;

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;

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
			while (true) {
				String data = in.readUTF(); // read a line of data from the stream
				//System.out.println(data);
				String [] parsedData = data.split("&");
				if(parsedData.length == 3) {
					boolean updated = GameModel.updateMap(Integer.valueOf(parsedData[0]), Integer.valueOf(parsedData[1]), Integer.valueOf(parsedData[2]));
					if(updated) {
						out.writeUTF("UPDATED WITH VALUE " + GameModel.getValue(Integer.valueOf(parsedData[0]), Integer.valueOf(parsedData[1])));
					}
				}
			}
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		}
	}
}
