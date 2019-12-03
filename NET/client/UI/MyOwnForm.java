package NET.client.UI;

import NET.gameModel.Action;
import NET.gameModel.PointFlag;
import shared.SharedTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class MyOwnForm extends AbstractForm{

    private JPanel rootPanel;
    private JPanel gameField;
    private JButton turnButton;
    private JLabel connectionStatusLabel;
    private JButton startGameButton;
    private ObjectOutputStream dos;
    private int squarePixelSize = 45;
    EnemyForm enemyForm;

    public MyOwnForm(ObjectOutputStream dos) {
        this.dos = dos;
        initFlet();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setContentPane(MyOwnForm.this.rootPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1100, 1000);
                frame.setResizable(false);
                frame.pack();
                frame.setVisible(true);
                createUIComponents();
                enemyForm = new EnemyForm(dos);
            }
        });
    }

    private void createUIComponents() {
        connectionStatusLabel.setVisible(false);
        gameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                connectionStatusLabel.setText("Mouse event: " + getSquareMapCoordinate(e.getX()) + " " + getSquareMapCoordinate(e.getY()));

                Graphics graphics = gameField.getGraphics();
                redrawGrid(graphics);
                int x = getSquareMapCoordinate(e.getX());
                int y = getSquareMapCoordinate(e.getY());
                updateGameField(graphics, x, y, true);
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
                if(turnButton.isVisible()) {
                    updateGameField(gameField.getGraphics());
                }
            }
        });
        turnButton.setVisible(false);
        turnButton.setEnabled(false);
        turnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnButton.setEnabled(false);
                try {
                    getDos().writeObject(new Action(gameMap));

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
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gameField.firePropertyChange(SharedTag.STATUS_OK, false, true);
                        enemyForm.fireUpdateEvent();
                    }
                });
            }
        });
    }


    public void notifyAboutTurn() {
        connectionStatusLabel.setText("Your turn");
        turnButton.setEnabled(true);
    }

    @Override
    protected boolean updateGameField(Graphics graphics, int xCoordinate, int yCoordinate, boolean fromClick) {
        graphics.setColor(new Color(115, 231, 118));
        graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        PointFlag currentCellValue = getMapCellValue(xCoordinate, yCoordinate);
        if(currentCellValue == PointFlag.FLEET_HIT) {
            graphics.setColor(Color.RED);
            graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            return false;
//        } else if (currentCellValue == -uniqueClientId) {
//            graphics.setColor(Color.RED);
//            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
//        } else if (currentCellValue < 0 && currentCellValue != -uniqueClientId){
//            graphics.setColor(Color.BLUE);
//            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        } else if (currentCellValue == PointFlag.EMPTY) {
            if(fromClick) {
                graphics.setColor(Color.BLUE);
                gameMap.get(xCoordinate).set(yCoordinate, PointFlag.FLEET);
                graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            } else return false;
            return false;
//        } else if (fromClick) {
//            gameMap[xCoordinate][yCoordinate] = -uniqueClientId;
//            graphics.setColor(Color.RED);
//            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        } else {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        }
        return true;
    }

    public void updateMap(int x, int y, PointFlag value) {
        gameMap.get(x).set(y, value);
    }

    public void updateMap(List<List<PointFlag>> newMap) {
        gameMap = newMap;
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

    public void setDos(ObjectOutputStream dos) {
        this.dos = dos;
    }

    protected ObjectOutputStream getDos() {
        return dos;
    }

}
