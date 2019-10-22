package NET;

import NET.UI.GameClient;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class TCPClient {
    public static void main(String args[]) {
        createClientConnection();
    }

    public static void createClientConnection() {
        Socket s = null;
        try {
            int serverPort = 7896;
            s = new Socket(InetAddress.getLocalHost(), serverPort);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            String string;

            GameClient gc = new GameClient();
            ClientConnection connection = new ClientConnection(s, gc);
            while (true) {
                string = scanner.next();
                //out.writeUTF(string); // UTF is a string encoding
                if (string.equals("exit")) {
                    s.close();
                    scanner.close();
                    return;
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage()); // host cannot be resolved
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage()); // end of stream reached
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage()); // error in reading the stream
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
        }
    }
}
