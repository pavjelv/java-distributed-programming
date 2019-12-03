package NET.client.UI;

import NET.gameModel.Action;
import NET.gameModel.Flag;
import NET.gameModel.Model;
import NET.gameModel.PointFlag;
import javafx.util.Pair;
import shared.SharedTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EnemyForm extends AbstractForm{
    private JPanel rootPanel;
    private JPanel gameField;
    private JButton turnButton;
    private JLabel connectionStatusLabel;
    private ObjectOutputStream dos;
    private Pair<Integer, Integer> currentAttemptCoordinate;


    public EnemyForm(ObjectOutputStream dos) {
        this.dos = dos;
        initFlet();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setContentPane(EnemyForm.this.rootPanel);
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
                if(currentAttemptCoordinate == null) {
                    Graphics graphics = gameField.getGraphics();
                    redrawGrid(graphics);
                    int x = getSquareMapCoordinate(e.getX());
                    int y = getSquareMapCoordinate(e.getY());
                    updateGameField(graphics, x, y, true);
                    currentAttemptCoordinate = new Pair<>(x, y);
                }
            }

        });

        gameField.addPropertyChangeListener(SharedTag.STATUS_OK, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Ok from server");
                if(currentAttemptCoordinate != null) {
                    updateMap(currentAttemptCoordinate.getKey(), currentAttemptCoordinate.getValue(), PointFlag.FLEET_HIT);
                }
                turnButton.setVisible(true);
                currentAttemptCoordinate = null;
                connectionStatusLabel.setText("Attempt successful");
                updateGameField(gameField.getGraphics());
            }
        });

        gameField.addPropertyChangeListener(SharedTag.UNSUCCESSFUL, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                currentAttemptCoordinate = null;
                connectionStatusLabel.setText("Attempt unsuccessful");
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

        turnButton.setEnabled(false);
        turnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentAttemptCoordinate != null) {
                    try {
                        turnButton.setVisible(false);
                        updateMap(currentAttemptCoordinate.getKey(), currentAttemptCoordinate.getValue(), PointFlag.EMPTY_ATTEMPTED);
                        getDos().writeObject(new Action(currentAttemptCoordinate));
                    } catch (IOException exception) {
                        currentAttemptCoordinate = null;
                        connectionStatusLabel.setText("Connection error occurred!");
                    }
                }
            }
        });
    }


    public void fireUnsuccessfulAttempt() {
        gameField.firePropertyChange(SharedTag.UNSUCCESSFUL, false, true);
    }

    public void notifyAboutTurn() {
        connectionStatusLabel.setText("Your turn");
        turnButton.setEnabled(true);
        turnButton.setVisible(true);
    }


    @Override
    protected boolean updateGameField(Graphics graphics, int xCoordinate, int yCoordinate, boolean fromClick) {
        graphics.setColor(new Color(255, 180, 80));
        graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        PointFlag currentCellValue = getMapCellValue(xCoordinate, yCoordinate);
        if(currentCellValue == PointFlag.FLEET_HIT) {
            graphics.setColor(Color.RED);
            graphics.fillOval(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            return false;
        } else if (currentCellValue == PointFlag.EMPTY) {
            if(fromClick) {
                graphics.setColor(Color.BLUE);
                gameMap.get(xCoordinate).set(yCoordinate, PointFlag.FLEET);
                graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
            } else return false;
            return false;
        } else {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(xCoordinate * squarePixelSize + 1, yCoordinate * squarePixelSize + 1, squarePixelSize - 1 , squarePixelSize - 1);
        }
        return true;
    }

    public JPanel getGameField() {
        return gameField;
    }

    public JLabel getConnectionStatusLabel() {
        return connectionStatusLabel;
    }


    protected ObjectOutputStream getDos() {
        return dos;
    }


}
