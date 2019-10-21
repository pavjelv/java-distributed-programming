package NET;

public class GameModel {
    private static final int[][] mapModel = new int[20][];

    static {
        initializeMap();
    }

    private static void initializeMap() {
        for (int i = 0; i < mapModel.length; i++) {
            mapModel[i] = new int[20];
            for (int j = 0; j < mapModel[i].length; j++) {
                mapModel[i][j] = 0;
            }
        }
    }

    public static boolean updateMap(int x, int y, int value) {
        synchronized (mapModel) {
            mapModel[x][y] = value;
            System.out.println(mapModel[x][y]);
            return true;
        }
    }

    public static int getValue(int x, int y) {
        synchronized (mapModel) {
            return mapModel[x][y];
        }
    }

}