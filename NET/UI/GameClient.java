package NET.UI;

import NET.SharedTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameClient {
    private JPanel rootPanel;
    private JPanel gameField;
    private JButton turnButton;
    private JLabel connectionStatusLabel;
    private DataOutputStream dos;

    public GameClient() {
        createUIComponents();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setContentPane(GameClient.this.rootPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1100, 1000);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        gameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                connectionStatusLabel.setText("Mouse event: " + e.getX());
            }
        });
        turnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    getDos().writeUTF(SharedTag.UPDATE_MAP_KEY + "&12&11&1337");
                } catch (IOException exception) {
                    connectionStatusLabel.setText("Connection error occurred!");
                }
            }
        });
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

    private class PanelWithGraphics extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.BLACK);
            for (int i = 30; i < gameField.getWidth(); i+=30) {
                graphics.drawLine(0, i, gameField.getWidth(), i);
                graphics.drawLine(i, 0, i, gameField.getHeight());
            }
        }
    }
}
