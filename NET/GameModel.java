package NET;

import java.util.concurrent.Semaphore;

public class GameModel {
    private static final int mapSize = 15;
    private static final int[][] mapModel = new int[mapSize][];

    private static Semaphore lock = new Semaphore(1, true);

    static {
        initializeMap();
    }

    private static void initializeMap() {
        for (int i = 0; i < mapModel.length; i++) {
            mapModel[i] = new int[mapSize];
            for (int j = 0; j < mapModel[i].length; j++) {
                mapModel[i][j] = 0;
            }
        }
    }

    public static boolean updateMap(int x, int y, int value) {
        synchronized (mapModel) {
            mapModel[x][y] = value;
            System.out.println("Map model is " + mapModel[x][y]);
            return true;
        }
    }

    public static int getValue(int x, int y) {
        synchronized (mapModel) {
            return mapModel[x][y];
        }
    }

    public static void lock() throws InterruptedException {
        lock.acquire();
    }

    public static void release() {
        lock.release();
    }

    public static int getMapSize() {
        return mapSize;
    }
}