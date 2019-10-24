package NET.UI;

import NET.SharedTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
                    updateGameField(graphics, getSquareMapCoordinate(e.getX()), getSquareMapCoordinate(e.getY()), true);
                    turnCount++;
                }
            }
        });
        gameField.addPropertyChangeListener(SharedTag.STATUS_OK, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("UPDATING GAME FIELD");
                updateGameField(gameField.getGraphics());
             }
        });
        gameField.addPropertyChangeListener(SharedTag.MODEL_UPDATE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("MAP RESULT IS ON CLIENT");
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
                    getDos().writeUTF(SharedTag.UPDATE_MAP_KEY + "&12&11&1337");
                } catch (IOException exception) {
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

    private void updateGameField(Graphics graphics, int xCoordinate, int yCoordinate, boolean fromClick) {
        int currentCellValue = gameMap[xCoordinate][yCoordinate];
        if(currentCellValue == uniqueClientId) {
            return;
        } else if (currentCellValue == -1){
            graphics.setColor(Color.BLACK);
            graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        } else if (currentCellValue == 0) {
            if(fromClick) {
                graphics.setColor(Color.RED);
                gameMap[xCoordinate][yCoordinate] = uniqueClientId;
                graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
            } else return;
        } else if (fromClick) {
            gameMap[xCoordinate][yCoordinate] = -1;
            graphics.setColor(Color.BLACK);
            graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        } else {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(xCoordinate * squarePixelSize, yCoordinate * squarePixelSize, squarePixelSize, squarePixelSize);
        }

    }

    public void updateMap(int x, int y, int value) {
        gameMap[x][y] = value;
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
