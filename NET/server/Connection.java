package NET.server;

import NET.shared.MapProcessor;
import NET.shared.SharedTag;
import NET.client.GameModel;

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
                String [] dataResponse = data.split(" ");
                if(dataResponse[0].equals(SharedTag.UPDATE_MAP_KEY)) {
                    processMapUpdate(dataResponse[1]);
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

    private void processMapUpdate(String data) {
        try {
            MapProcessor.updateMap(GameModel.getMapModel(), data);
            out.writeUTF(SharedTag.STATUS_OK);
        } catch (IOException e) {
            System.out.println("Error while map update: " + e.getMessage());
        }
    }

    private void sendUpdatedMapToClient() {
        try {
            out.writeUTF(SharedTag.MODEL_UPDATE + " " + MapProcessor.serializeMap(GameModel.getCopyOfMap(), GameModel.getMapSize()));
        } catch (IOException e) {
            System.out.println("Error while map update: " + e.getMessage());
        }
    }
}
