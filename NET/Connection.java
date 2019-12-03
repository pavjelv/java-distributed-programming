package NET;

import NET.gameModel.*;

import java.io.*;
import java.net.*;
import java.util.List;

class Connection extends Thread {
    ObjectInputStream inRed;
    ObjectOutputStream outRed;
    Socket clientSocketRed;
    ObjectInputStream inBlue;
    ObjectOutputStream outBlue;
    Socket clientSocketBlue;

    public Connection(Socket aClientSocketRed, Socket aClientSocketBlue) {
        try {
            //Creating connection with player red
            clientSocketRed = aClientSocketRed;
            inRed = new ObjectInputStream(clientSocketRed.getInputStream());
            System.out.println("red input stream created");
            outRed = new ObjectOutputStream(clientSocketRed.getOutputStream());
            System.out.println("red output stream created");

            //Creating connection with player blue
            clientSocketBlue = aClientSocketBlue;
            inBlue = new ObjectInputStream(clientSocketBlue.getInputStream());
            System.out.println("blue input stream created");
            outBlue = new ObjectOutputStream(clientSocketBlue.getOutputStream());
            System.out.println("blue output stream created");

            outRed.writeObject(new Action(Id.RED));
            outBlue.writeObject(new Action(Id.BLUE));

            // reading start game flag
            System.out.println("reading start game flags");
            inRed.readObject();
            inBlue.readObject();

            this.start();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection:" + e.getCause());
            e.printStackTrace();
        }
    }

    public void run() { // an echo server
        List<List<PointFlag>> blueMap = null;
        List<List<PointFlag>> redMap = null;
        Integer redCount = 20;
        Integer blueCount = 20;
        try {
            System.out.println("game started");
            //reading maps
            Action b = (Action) inBlue.readObject();
            Action r = (Action) inRed.readObject();
            System.out.println("Fleet is set up");
            if (b.getType().equals(r.getType()) && b.getType().equals(Flag.SET_FLEET)) {
                blueMap = b.getMap();
                redMap = r.getMap();
            } else {
                System.err.println("Game is not started");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (checkIfSomeoneWon(redCount, blueCount)) return;
                outRed.writeObject(new Action(Flag.WAITING_FOR_YOUR_TURN));
                boolean willRedMakeTurn = true;
                while(willRedMakeTurn){
                    outRed.writeObject(new Action(Flag.WAITING_FOR_YOUR_TURN));
                    Action actionRed = (Action) inRed.readObject();
                    System.out.println(actionRed.toString());
                    if (actionRed.getType().equals(Flag.TRY)) {

                        Integer redAttemptX = actionRed.getAttemptCoordinates().getKey();
                        Integer redAttemptY = actionRed.getAttemptCoordinates().getValue();
                        switch (blueMap.get(redAttemptX).get(redAttemptY)) {
                            case EMPTY:
                                blueMap.get(redAttemptX).set(redAttemptY, PointFlag.EMPTY_ATTEMPTED);
                                willRedMakeTurn = false;
                                break;
                            case FLEET:
                                blueMap.get(redAttemptX).set(redAttemptY, PointFlag.FLEET_HIT);
                                willRedMakeTurn = --blueCount > 0;
                                break;
                            default:
                                outRed.writeObject(new Action(Flag.INVALID_TURN, actionRed.getAttemptCoordinates()));
                                willRedMakeTurn = true;
                                break;
                        }
                    }
                }

                if (checkIfSomeoneWon(redCount, blueCount)) return;
                outBlue.writeObject(new Action(Flag.WAITING_FOR_YOUR_TURN));
                boolean willBlueMakeTurn = true;
                while (willBlueMakeTurn) {
                    outBlue.writeObject(new Action(Flag.WAITING_FOR_YOUR_TURN));
                    Action actionBlue = (Action) inBlue.readObject();
                    System.out.println(actionBlue.toString());
                    if (actionBlue.getType().equals(Flag.TRY)) {
                        Integer blueAttemptX = actionBlue.getAttemptCoordinates().getKey();
                        Integer blueAttemptY = actionBlue.getAttemptCoordinates().getValue();
                        switch (redMap.get(blueAttemptX).get(blueAttemptY)) {
                            case EMPTY:
                                redMap.get(blueAttemptX).set(blueAttemptY, PointFlag.EMPTY_ATTEMPTED);

                                willBlueMakeTurn = false;
                                break;
                            case FLEET:
                                redMap.get(blueAttemptX).set(blueAttemptY, PointFlag.FLEET_HIT);
                                willBlueMakeTurn = --redCount > 0;
                                break;
                            default:
                                outBlue.writeObject(new Action(Flag.INVALID_TURN, actionBlue.getAttemptCoordinates()));
                                willBlueMakeTurn = false;
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfSomeoneWon(Integer redCount, Integer blueCount) throws IOException {
        if (blueCount == 0) {
            someoneWon(true);
            outRed.close();
            outBlue.close();
            clientSocketRed.close();
            clientSocketBlue.close();
            return true;
        }
        if (redCount == 0) {
            someoneWon(false);
            outRed.close();
            outBlue.close();
            clientSocketRed.close();
            clientSocketBlue.close();
            return true;
        }
        return false;
    }

    private void someoneWon(boolean redWon) {
        try {
            outRed.writeObject(new Action(redWon ? Flag.WIN : Flag.LOST));
            outBlue.writeObject(new Action(!redWon ? Flag.WIN : Flag.LOST));
            System.err.println("Game finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
