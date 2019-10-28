package NET.client;

import NET.shared.MapProcessor;
import NET.shared.SharedTag;
import NET.client.UI.GameClient;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class ClientConnection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket serverSocket;
    GameClient client;

    public ClientConnection(Socket serverSocket) {
        this.client = new GameClient();
        try {
            this.serverSocket = serverSocket;
            in = new DataInputStream(serverSocket.getInputStream());
            out = new DataOutputStream(serverSocket.getOutputStream());
            client.setDos(out);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ClientConnection.this.start();
                }
            });
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String data = in.readUTF();
                String [] dataResponse = data.split(" ");
                if(dataResponse[0].equals(SharedTag.STATUS_OK)) {
                    client.getConnectionStatusLabel().setText("Successfully updated!");
                    client.getGameField().firePropertyChange(SharedTag.STATUS_OK, true, false);
                } else if(dataResponse[0].equals(SharedTag.MODEL_UPDATE)) {
                    client.getConnectionStatusLabel().setText("Your turn");
                    System.out.println("Data received");
                    client.updateMap(MapProcessor.deserializeMap(dataResponse[1]));
                    client.getGameField().firePropertyChange(SharedTag.MODEL_UPDATE, 1, 5);
                } else if(dataResponse[0].equals(SharedTag.BASE_COORDINATES)) {
                    String [] baseCoordinates = dataResponse[1].split(SharedTag.COORDINATE_SEPARATOR);
                    client.setBaseX(Integer.valueOf(baseCoordinates[0]));
                    client.setBaseY(Integer.valueOf(baseCoordinates[1]));
                    client.fulfillBaseCell();
                }
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }
    }
}