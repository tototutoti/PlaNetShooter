package view.client.game_frame;

import view.client.game_frame.game_only.GamePanel;
import view.client.game_frame.menu.GameMenuPanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel gamePanel = new GamePanel();
    public GameFrame (String clientName) {
        super();
        this.setTitle("PlaNetShooter Client : (" +clientName+ ")");
        this.setSize(768, 432);
        this.setMinimumSize(new Dimension(574, 330));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        GameMenuPanel gameMenuPanel = new GameMenuPanel();
        gameMenuPanel.setPreferredSize(new Dimension(this.getWidth(), 30));
        this.add(gameMenuPanel, BorderLayout.NORTH);

        this.add(gamePanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}