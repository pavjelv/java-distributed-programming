import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String args[]) throws IOException {
        Socket clientSocket1 = null;
        Socket clientSocket2 = null;
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort); // new server port generated
            while (true) {
                clientSocket1 = listenSocket.accept(); // listen for new connection
                System.out.println("Player 1 connected");
                clientSocket2 = listenSocket.accept(); // listen for new connection
                System.out.println("Player 2 connected");
                Connection c = new Connection(clientSocket1, clientSocket2); // launch new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (clientSocket1 != null) {
                clientSocket1.close();
            }
            if (clientSocket2 != null) {
                clientSocket2.close();
            }
        }
    }
}
// писть в клиете сообщения жать энтр и они печатают на сервере 