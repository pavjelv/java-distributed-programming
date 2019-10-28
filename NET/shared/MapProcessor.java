package NET.shared;

public final class MapProcessor {
    public static String serializeMap(int [][] map, int mapSize) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                result.append(map[i][j]).append(SharedTag.CELL_SEPARATOR);
            }
            result.deleteCharAt(result.length() - 1);
            result.append(SharedTag.ROW_SEPARATOR);
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static int[][] deserializeMap(String serializedMap) {
        String[] rows = serializedMap.split(SharedTag.ROW_SEPARATOR);
        int[][] result = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].split(SharedTag.CELL_SEPARATOR);
            result[i] = new int[cells.length];
            for (int j = 0; j < cells.length; j++) {
                result[i][j] = Integer.valueOf(cells[j]);
            }
        }
        return result;
    }

    public static int[][] updateMap (int[][] map, String data) {
        String[] cellsModel = data.split(SharedTag.CELL_SEPARATOR);
        for (int i = 0; i < cellsModel.length; i++) {
            String[] coordinatesAndValue = cellsModel[i].split(SharedTag.COORDINATE_SEPARATOR);
            map[Integer.valueOf(coordinatesAndValue[0])][Integer.valueOf(coordinatesAndValue[1])] = Integer.valueOf(coordinatesAndValue[2]);
        }
        return map;
    }
}
