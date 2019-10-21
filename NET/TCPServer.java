package NET;

import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;


public class TCPServer {
    private static final Set<Connection> connectionSet = new HashSet<>();

    public static void main (String args[]) {
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort); // new server port generated
            GameModel gameModel = new GameModel();
            while(true) {
                Socket clientSocket = listenSocket.accept(); // listen for new connection
                System.out.println("Client connected");
                Connection c = new Connection(clientSocket); // launch new thread
                synchronized (connectionSet) {
                    connectionSet.add(c);
                }
            }
        } catch(IOException e) { System.out.println("Listen socket:"+e.getMessage());
        }
    }

}
// писть в клиете сообщения жать энтр и они печатают на сервере 