package NET.client.UI;

import NET.shared.SharedTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameClient {
    private JPanel rootPanel;
    private JPanel gameField;
    private JButton turnButton;
    private JLabel connectionStatusLabel;
    private JButton startGameButton;
    private DataOutputStream dos;
    private int squarePixelSize = 30;
    private int [][] gameMap;
    private final int uniqueClientId = (int)System.currentTimeMillis();
    private int turnCount = 0;
    private StringBuilder currentChangedCells = new StringBuilder();
    private int baseX = 5;
    private int baseY = 5;

    public GameClient() {
        gameMap = new int[15][];
        for (int i = 0; i < gameMap.length; i++) {
            gameMap[i] = new int[15];
            for (int j = 0; j < gameMap[i].length; j++) {
                gameMap[i][j] = 0;
            }
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setContentPane(GameClient.this.rootPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1100, 1000);
                frame.setResizable(false);
                frame.pack();
                frame.setVisible(true);
                createUIComponents();
            }
        });
    }

    private void createUIComponents() {
        connectionStatusLabel.setVisible(false);
        gameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                connectionStatusLabel.setText("Mouse event: " + getSquareMapCoordinate(e.getX()) + " " + getSquareMapCoordinate(e.getY()));
                if(turnCount < 5 && turnButton.isEnabled()) {
                    Graphics graphics = gameField.getGraphics();
                    redrawGrid(graphics);
                    int x = getSquareMapCoordinate(e.getX());
                    int y = getSquareMapCoordinate(e.getY());
                    if (updateGameField(graphics, x, y, true)) {
                        turnCount++;
                    }
                    drawAvailableCells(graphics);
                    currentChangedCells.append(x).append(SharedTag.COORDINATE_SEPARATOR)
                            .append(y).append(SharedTag.COORDINATE_SEPARATOR)
                            .append(getMapCellValue(x, y)).append(SharedTag.CELL_SEPARATOR);
                }
            }
        });
        gameField.addPropertyChangeListener(SharedTag.STATUS_OK, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Ok from server");
                updateGameField(gameField.getGraphics());
                drawAvailableCells(gameField.getGraphics());
             }
        });
        gameField.addPropertyChangeListener(SharedTag.MODEL_UPDATE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Model is updated from server");
                turnButton.setEnabled(true);
                updateGameField(gameField.getGraphics());
                drawAvailableCells(gameField.getGraphics());
                turnCount = 0;
            }
        });
        turnButton.setVisible(false);
        turnButton.setEnabled(false);
        turnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnButton.setEnabled(false);
                try {
                    getDos().writeUTF(SharedTag.UPDATE_MAP_KEY + " "
                            + currentChangedCells.deleteCharAt(currentChangedCells.length() - 1).toString());
                    currentChangedCells = new StringBuilder();
                } catch (IOException exception) {
                    currentChangedCells = new StringBuilder();
                    connectionStatusLabel.setText("Connection error occurred!");
                }
            }
        });

        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnButton.setVisible(true);
                startGameButton.setVisible(false);
                connectionStatusLabel.setVisible(true);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gameField.firePropertyChange(SharedTag.STATUS_OK, false, true);
                    }
                });
            }
        });
    }

    private void updateGameField(Graphics graphics) {
        redrawGrid(graphics);
        for (int i = 0; i < gameMap.length; i++) {
            for (int j = 0; j < gameMap[i].length; j++) {
                updateGameField(graphics, i, j, false);
            }
        }
    }

    private int getMapCellValue(int x, int y) {
        return gameMap[x][y];
    }

    private boolean updateGameField(Graphics graphics, int xCoordinate, int yCoordinate, boolean fromClick) {
        graphics.setColor(new Color(115, 231, 118));
        graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        int currentCellValue = gameMap[xCoordinate][yCoordinate];
        if(currentCellValue == uniqueClientId) {
            graphics.setColor(Color.RED);
            graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            return false;
        } else if (currentCellValue == -uniqueClientId) {
            graphics.setColor(Color.RED);
            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        } else if (currentCellValue < 0 && currentCellValue != -uniqueClientId){
            graphics.setColor(Color.BLUE);
            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        } else if (currentCellValue == 0) {
            if(fromClick) {
                graphics.setColor(Color.RED);
                gameMap[xCoordinate][yCoordinate] = uniqueClientId;
                graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            } else return false;
        } else if (fromClick) {
            gameMap[xCoordinate][yCoordinate] = -uniqueClientId;
            graphics.setColor(Color.RED);
            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        } else {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        }
        return true;
    }

    public void updateMap(int x, int y, int value) {
        gameMap[x][y] = value;
    }

    public void updateMap(int[][] newMap) {
        gameMap = newMap;
        gameMap[baseX][baseY] = uniqueClientId;
    }

    private void redrawGrid(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        for (int i = squarePixelSize; i < gameField.getWidth(); i+=squarePixelSize) {
            graphics.drawLine(0, i, gameField.getWidth(), i);
            graphics.drawLine(i, 0, i, gameField.getHeight());
        }
    }

    private int getSquareMapCoordinate(int x) {
        return x/squarePixelSize;
    }

    public JPanel getGameField() {
        return gameField;
    }

    public JLabel getConnectionStatusLabel() {
        return connectionStatusLabel;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    protected DataOutputStream getDos() {
        return dos;
    }

    private void drawAvailableCells(Graphics g) {
        g.setColor(Color.RED);
        for (int i = 0; i < gameMap.length; i++) {
            for (int j = 0; j < gameMap[i].length; j++) {
                if(gameMap[i][j] == uniqueClientId) {
                    checkBoundsAndDrawAvailableCells(g, i, j);
                } else if(gameMap[i][j] == -uniqueClientId) {
                    boolean turn = resolveAbilityToTurn(i, j);
                    if(turn) {
                        checkBoundsAndDrawAvailableCells(g, i, j);
                    }
                }
            }
        }
    }

    private void checkBoundsAndDrawAvailableCells(Graphics g, int i, int j) {
        for(int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                int x = i + k;
                int y = j + l;
                if (x >= 0 && y >= 0 && x < gameMap.length && y < gameMap.length) {
                    if (gameMap[x][y] == 0 || gameMap[x][y] > 0 && gameMap[x][y] != uniqueClientId) {
                        g.drawOval(x * squarePixelSize + (squarePixelSize / 2) - 3, y * squarePixelSize + (squarePixelSize / 2) - 3, 6, 6);
                    }
                }
            }
        }
    }

    private boolean resolveAbilityToTurn(int i, int j) {
        if(i == baseX && j == baseY) {
            return true;
        }
        for(int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                int x = i + k;
                int y = j + l;
                if (x >= 0 && y >= 0 && x < gameMap.length && y < gameMap.length) {
                    if (gameMap[x][y] == uniqueClientId) {
                        gameMap[x][y] = 1;
                        if(resolveAbilityToTurn(x, y)) {
                            gameMap[x][y] = uniqueClientId;
                            return true;
                        } else {
                            gameMap[x][y] = uniqueClientId;
                        }
                    } else if (gameMap[x][y] == -uniqueClientId) {
                        gameMap[x][y] = -1;
                        if(resolveAbilityToTurn(x, y)) {
                            gameMap[x][y] = -uniqueClientId;
                            return true;
                        } else {
                            gameMap[x][y] = -uniqueClientId;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setBaseX(int baseX) {
        this.baseX = baseX;
    }

    public void setBaseY(int baseY) {
        this.baseY = baseY;
    }

    public void fulfillBaseCell () {
        currentChangedCells.append(baseX).append(SharedTag.COORDINATE_SEPARATOR)
                .append(baseY).append(SharedTag.COORDINATE_SEPARATOR)
                .append(uniqueClientId).append(SharedTag.CELL_SEPARATOR);
    }
}
