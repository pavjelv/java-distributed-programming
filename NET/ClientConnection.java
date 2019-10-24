package NET;

import NET.UI.GameClient;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Thread {
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
                if(data.startsWith(SharedTag.STATUS_OK)) {
                    client.getConnectionStatusLabel().setText("Successfully updated!");
                    client.updateMap(10, 11, 50);
                    client.getGameField().firePropertyChange(SharedTag.STATUS_OK, true, false);
                } else if(data.startsWith(SharedTag.MODEL_UPDATE)) {
                    client.getConnectionStatusLabel().setText("Your turn");
                    System.out.println("Data received");
                    System.out.println(data);
                    client.getGameField().firePropertyChange(SharedTag.MODEL_UPDATE, 1, 5);
                }
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }
    }
}