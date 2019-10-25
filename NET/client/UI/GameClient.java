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
             }
        });
        gameField.addPropertyChangeListener(SharedTag.MODEL_UPDATE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Model is updated from server");
                turnButton.setEnabled(true);
                updateGameField(gameField.getGraphics());
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
            }
        });
    }

    private void updateGameField(Graphics graphics) {
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
        int currentCellValue = gameMap[xCoordinate][yCoordinate];
        if(currentCellValue == uniqueClientId) {
            graphics.setColor(Color.RED);
            graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
            return false;
        } else if (currentCellValue == -uniqueClientId) {
            graphics.setColor(Color.RED);
            graphics.fillOval(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        } else if (currentCellValue < 0 && currentCellValue != -uniqueClientId){
            graphics.setColor(Color.BLUE);
            graphics.fillOval(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        } else if (currentCellValue == 0) {
            if(fromClick) {
                graphics.setColor(Color.RED);
                gameMap[xCoordinate][yCoordinate] = uniqueClientId;
                graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
            } else return false;
        } else if (fromClick) {
            gameMap[xCoordinate][yCoordinate] = -uniqueClientId;
            graphics.setColor(Color.RED);
            graphics.fillOval(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        } else {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        }
        return true;
    }

    public void updateMap(int x, int y, int value) {
        gameMap[x][y] = value;
    }

    public void updateMap(int[][] newMap) {
        gameMap = newMap;
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

    private class PanelWithGraphics extends JComponent  {
        PanelWithGraphics() {
            setPreferredSize(new Dimension(500, 500));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.BLACK);
            for (int i = 30; i < getWidth(); i+=30) {
                graphics.drawLine(0, i, getWidth(), i);
                graphics.drawLine(i, 0, i, getHeight());
            }
        }
    }
}
