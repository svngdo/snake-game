import javax.swing.*;

public class SnakeGame extends JFrame {
    private final GamePanel gamePanel = new GamePanel();

    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(gamePanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void start() {
        gamePanel.startGameThread();
    }
}
