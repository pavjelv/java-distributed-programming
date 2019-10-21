import java.net.*;
import java.io.*;
public class TCPServer {
    public static void main (String args[]) {
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort); // new server port generated
            while(true) {
                Socket clientSocket = listenSocket.accept(); // listen for new connection
                System.out.println("Recived message");
                Connection c = new Connection(clientSocket); // launch new thread
            }
        } catch(IOException e) { System.out.println("Listen socket:"+e.getMessage());
        }
    }
}
// писть в клиете сообщения жать энтр и они печатают на сервере 