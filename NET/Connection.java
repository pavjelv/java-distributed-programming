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
                try {
                    System.out.println("Locked");
                    GameModel.lock();
                } catch (InterruptedException e) {
                    GameModel.release();
                    e.printStackTrace();
                }
                sendUpdatedMapToClient();
                String data = in.readUTF(); // read a line of data from the stream
                String [] parsedData = data.split("&");
                if(parsedData[0].equals(SharedTag.UPDATE_MAP_KEY)) {
                    processMapUpdate(parsedData);
                }
                System.out.println("Lock is released");
                GameModel.release();
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            try {
                GameModel.release();
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
                    out.writeUTF(SharedTag.STATUS_OK + " " + GameModel.getValue(Integer.valueOf(data[1]), Integer.valueOf(data[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error while map update: " + e.getMessage());
        }
    }

    private void sendUpdatedMapToClient() {
        try {
            out.writeUTF(SharedTag.MODEL_UPDATE + " " + serializeMap());
        } catch (IOException e) {
            System.out.println("Error while map update: " + e.getMessage());
        }
    }

    private String serializeMap() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < GameModel.getMapSize(); i++) {
            for (int j = 0; j < GameModel.getMapSize(); j++) {
                result.append(i).append(SharedTag.COORDINATE_SEPARATOR).append(j)
                        .append(SharedTag.COORDINATE_SEPARATOR).append(GameModel.getValue(i,j))
                        .append(SharedTag.CELL_SEPARATOR);
            }
            result.deleteCharAt(result.length() - 1);
            result.append(SharedTag.ROW_SEPARATOR);
        }
        return result.toString();
    }
}
