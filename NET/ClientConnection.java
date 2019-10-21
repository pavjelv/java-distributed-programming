package NET;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class ClientConnection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket serverSocket;

    public ClientConnection(Socket serverSocket) {
        try {
            this.serverSocket = serverSocket;
            in = new DataInputStream(serverSocket.getInputStream());
            out = new DataOutputStream(serverSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() { // an echo server
        try {
            while (true) {
                String data = in.readUTF();
                System.out.println("Got data from server " + data); // read a line of data from the stream
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }
    }
}