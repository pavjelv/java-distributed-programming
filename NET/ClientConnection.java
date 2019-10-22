package NET;

import NET.UI.GameClient;

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

    public ClientConnection(Socket serverSocket, GameClient gameClient) {
        this.client = gameClient;
        try {
            this.serverSocket = serverSocket;
            in = new DataInputStream(serverSocket.getInputStream());
            out = new DataOutputStream(serverSocket.getOutputStream());
            gameClient.setDos(out);
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String data = in.readUTF();
                if(data.startsWith(SharedTag.MODEL_UPDATE)) {
                    client.getConnectionStatusLabel().setText(data);
                }
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }
    }
}