import gameModel.*;
import javafx.util.Pair;

import javax.naming.OperationNotSupportedException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.stream.Collectors;

public class TCPClient {
    private static final int SIZE = 10;
    private static Scanner scanner = new Scanner(System.in);
    private static int testc = 0;
    public static void main(String args[]) throws ClassNotFoundException {
        Socket s = null;
        try {
            int serverPort = 7896;
            s = new Socket(InetAddress.getLocalHost(), serverPort);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            System.out.println("OUT");
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            Model m = new Model(10);
            System.out.println("IN");
            out.writeObject(m);
            System.out.println("wrote");
            String str = (String) in.readObject();
            System.out.println(str);
            startGame(in, out);
        } catch (UnknownHostException | OperationNotSupportedException e) {
            System.out.println("Socket:" + e.getMessage()); // host cannot be resolved
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage()); // end of stream reached
        } catch (IOException e) {
            e.printStackTrace(); // error in reading the stream
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
        }
    }

    private static void startGame(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, OperationNotSupportedException {
        Action a = (Action) in.readObject();
        Id id = null;

        List<List<PointFlag>> myMap = null;
        List<List<PointFlag>> opponentMap = new ArrayList<List<PointFlag>>(SIZE);

        for (int i = 0; i < SIZE; i++) {
            List<PointFlag> list = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                list.add(PointFlag.EMPTY);
            }
            opponentMap.add(list);
        }

        if (!a.getType().equals(Flag.START)) {
            throw new OperationNotSupportedException("game has not started");
        }

        id = a.getId();

        myMap = setFlet();
        out.writeObject(new Action(myMap));
        while (true) {
            Action nextAction = (Action) in.readObject();
            System.out.println(nextAction.toString());
            switch (nextAction.getType()) {
                case WAITING_FOR_YOUR_TURN:
//                    makeNextTurn();
                    out.writeObject(new Action(getTurnCoordinates()));
                    break;
                case WIN:
                    System.err.println("you won");
                    return;
                case LOST:
                    System.err.println("you lost");
                    return;
                case INVALID_TURN:
                    System.err.println("you made wrong turn");
                    break;
                case ATTEMPT_SUCCESSFUL: markPoint(true, nextAction.getAttemptCoordinates(), opponentMap); break;
                case ATTEMPT_UNSUCCESSFUL: markPoint(false, nextAction.getAttemptCoordinates(), opponentMap); break;
                default:
                    System.err.println("something wrong" + nextAction.toString());
            }
        }
    }

    private static void markPoint(boolean successful, Pair<Integer, Integer> attemptCoordinates, List<List<PointFlag>> opponentMap) {
        opponentMap.get(attemptCoordinates.getKey()).set(attemptCoordinates.getValue(), successful ? PointFlag.FLEET_HIT : PointFlag.EMPTY_ATTEMPTED);
    }

    private static void makeNextTurn() {
        Pair<Integer, Integer> nextTurn = getTurnCoordinates();
    }

    private static Pair<Integer, Integer> getTurnCoordinates() {
//        System.out.print("Enter next coordinates, x: ");
//        int x = scanner.nextInt();
//        System.out.println();
//        System.out.print("y:");
//        int y = scanner.nextInt();
//        System.out.println();

        return getMockPoint();

//        return new Pair<>(x,y);
    }

    private static List<List<PointFlag>> setFlet() {
        List<List<PointFlag>> map = new ArrayList<List<PointFlag>>(SIZE);

        for (int i = 0; i < SIZE; i++) {
            List<PointFlag> list = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                list.add(PointFlag.EMPTY);
            }
            map.add(list);
        }
        List<Integer> remainingFleet = new ArrayList<>(4);
        remainingFleet.add(4);
        remainingFleet.add(3);
        remainingFleet.add(2);
        remainingFleet.add(1);
        // first set 4
        System.out.println("set 4");

        Pair<Integer, Integer> point = getTurnCoordinates();
        List<Pair<Integer, Integer>> listOfPoints = new ArrayList<>();
        listOfPoints.add(point);

        while (listOfPoints.size() < 4) {
            Pair<Integer, Integer> nextPoint = getTurnCoordinates();
            Pair<Integer, Integer> matchingPointInList = listOfPoints.stream().filter(pointInList ->
                    findBlockDistance(nextPoint, pointInList) == 0).findFirst().orElse(null);

            if (matchingPointInList != null) {
                listOfPoints.remove(matchingPointInList);
            } else {
                Pair<Integer, Integer> closePointInList = listOfPoints.stream().filter(pointInList ->
                        findBlockDistance(nextPoint, pointInList) == 1).findFirst().orElse(null);
                if (closePointInList == null) {
                    System.err.println("you made wrong turn");
                } else {
                    listOfPoints.add(nextPoint);
                }
            }
        }
        // now set 3s
        set3s(listOfPoints);
        System.out.println(listOfPoints);

        // now set 2s
        nowSet2s(listOfPoints);
        System.out.println("set 1");
        System.out.println(listOfPoints);


        List<Pair<Integer, Integer>> listof1s = new ArrayList<>();
        while (listof1s.size() < 4) {
            Pair<Integer, Integer> nextPoint = getTurnCoordinates();
            Pair<Integer, Integer> samePoint = listof1s.stream().filter(p -> findBlockDistance(nextPoint, p) == 0).findFirst().orElse(null);
            if (samePoint == null) listof1s.add(nextPoint);
            else listof1s.remove(samePoint);
        }
        listOfPoints.addAll(listof1s);
        System.out.println(listOfPoints);
        //now add all ppoints to map

        listOfPoints.forEach(p -> map.get(p.getKey()).set(p.getValue(), PointFlag.FLEET));

        return map;
    }

    private static void nowSet2s(List<Pair<Integer, Integer>> listOfPoints) {
        System.out.println("set 2");

        List<List<Pair<Integer, Integer>>> listOf2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            listOf2.add(new ArrayList<>());
        }

        while (listOf2.stream().allMatch(list -> list.size() < 2)) {
            Pair<Integer, Integer> nextPoint = getTurnCoordinates();
            //find if there is such coordinate in 3
            List<Pair<Integer, Integer>> matchingShip = listOf2.stream().filter(list ->
                    list.stream().filter(pointInList ->
                            findBlockDistance(nextPoint, pointInList) == 0).findFirst().orElse(null) != null).findFirst().orElse(null);
            if (matchingShip != null) {
                matchingShip = matchingShip.stream().filter(p -> p.getValue().equals(nextPoint.getValue()) && p.getKey().equals(nextPoint.getKey())).collect(Collectors.toList());
                continue;
            }
            // find if there is a matching ship or an empty one
            matchingShip = listOf2.stream().filter(list ->
                    list.stream().filter(pointInList ->
                            findBlockDistance(nextPoint, pointInList) == 1).findFirst().orElse(null) != null).findFirst().orElse(null);
            if (matchingShip == null) {
                matchingShip = listOf2.stream().filter(ship -> ship.size() == 0).findFirst().orElse(null);
                if (matchingShip == null) continue;
                matchingShip.add(nextPoint);
            } else {
                if (matchingShip.size() == 3) continue;
                matchingShip.add(nextPoint);
            }
        }
        listOf2.stream().forEach(list -> list.forEach(p -> listOfPoints.add(p)));
    }

    private static List<List<Pair<Integer, Integer>>> set3s(List<Pair<Integer, Integer>> listOfPoints) {
        System.out.println("set 3");
        List<List<Pair<Integer, Integer>>> listOf3 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            listOf3.add(new ArrayList<>());
        }

        while (listOf3.stream().allMatch(list -> list.size() < 3)) {
            Pair<Integer, Integer> nextPoint = getTurnCoordinates();
            //find if there is such coordinate in 3
            List<Pair<Integer, Integer>> matchingShip = listOf3.stream().filter(list ->
                    list.stream().filter(pointInList ->
                            findBlockDistance(nextPoint, pointInList) == 0).findFirst().orElse(null) != null).findFirst().orElse(null);
            if (matchingShip != null) {
                matchingShip = matchingShip.stream().filter(p -> p.getValue().equals(nextPoint.getValue()) && p.getKey().equals(nextPoint.getKey())).collect(Collectors.toList());
                continue;
            }
            // find if there is a matching ship or an empty one
            matchingShip = listOf3.stream().filter(list ->
                    list.stream().filter(pointInList ->
                            findBlockDistance(nextPoint, pointInList) == 1).findFirst().orElse(null) != null).findFirst().orElse(null);
            if (matchingShip == null) {
                matchingShip = listOf3.stream().filter(ship -> ship.size() == 0).findFirst().orElse(null);
                if (matchingShip == null) continue;
                System.out.println("created new ship");
                matchingShip.add(nextPoint);
            } else {
                if (matchingShip.size() == 3) continue;
                System.out.println("added to old ship");

                matchingShip.add(nextPoint);
            }
        }
        listOf3.stream().forEach(list -> list.forEach(p -> listOfPoints.add(p)));
        return listOf3;
    }

    private static int findBlockDistance(Pair<Integer, Integer> nextPoint, Pair<Integer, Integer> pointInList) {
        return Math.abs(pointInList.getKey() - nextPoint.getKey()) +
                Math.abs(pointInList.getValue() - nextPoint.getValue());
    }
    private static Pair<Integer, Integer> getMockPoint() {
        List<Pair<Integer, Integer>> ml = new ArrayList<>();
        ml.add(new Pair<>(0,0));
        ml.add(new Pair<>(0,1));
        ml.add(new Pair<>(0,2));
        ml.add(new Pair<>(0,3));

        ml.add(new Pair<>(3,3));
        ml.add(new Pair<>(3,4));
        ml.add(new Pair<>(3,2));

        ml.add(new Pair<>(1,5));
        ml.add(new Pair<>(1,6));
        ml.add(new Pair<>(1,7));

        ml.add(new Pair<>(5,5));
        ml.add(new Pair<>(5,6));

        ml.add(new Pair<>(5,8));
        ml.add(new Pair<>(5,9));

        ml.add(new Pair<>(2,5));
        ml.add(new Pair<>(2,6));

        ml.add(new Pair<>(2,8));
        ml.add(new Pair<>(2,9));

        ml.add(new Pair<>(8,7));
        ml.add(new Pair<>(8,9));
        ml.add(new Pair<>(9,6));
        ml.add(new Pair<>(9,1));

        if (testc  + 1 == ml.size() ) {
            testc = 0;
        }
        return ml.get(testc ++);
    }
}
