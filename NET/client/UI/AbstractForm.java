package NET.client.UI;

import NET.gameModel.PointFlag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractForm {
    public static final int SIZE = 10;
    protected List<List<PointFlag>> gameMap;
    protected int squarePixelSize = 45;

    protected void initFlet() {
        List<List<PointFlag>> map = new ArrayList<List<PointFlag>>(SIZE);

        for (int i = 0; i < SIZE; i++) {
            List<PointFlag> list = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                list.add(PointFlag.EMPTY);
            }
            map.add(list);
        }
        gameMap = map;
    }

    protected void updateGameField(Graphics graphics) {
        redrawGrid(graphics);
        for (int i = 0; i < gameMap.size(); i++) {
            for (int j = 0; j < gameMap.get(i).size(); j++) {
                updateGameField(graphics, i, j, false);
            }
        }
    }

    protected void redrawGrid(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        for (int i = squarePixelSize; i < getGameField().getWidth(); i+=squarePixelSize) {
            graphics.drawLine(0, i, getGameField().getWidth(), i);
            graphics.drawLine(i, 0, i, getGameField().getHeight());
        }
    }

    protected PointFlag getMapCellValue(int x, int y) {
        return gameMap.get(x).get(y);
    }

    protected abstract boolean updateGameField(Graphics graphics, int xCoordinate, int yCoordinate, boolean fromClick);

    protected abstract JPanel getGameField();
}
