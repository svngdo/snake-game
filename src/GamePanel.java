import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int GRID_SIZE = 20;
    public static final int CELL_SIZE = 20;

    private static final int FPS = 10;
    private static final double DRAW_INTERVAL = Math.pow(10, 9) / FPS; // 16,666,666.66666667 nanosecond to draw next frame

    private final Thread gameThread = new Thread(this);

    private final JPanel[][] grid = new JPanel[GRID_SIZE][GRID_SIZE];
    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private Direction direction = Direction.NONE;
    private boolean running;

    public GamePanel() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE, 0, 0));

        addKeyListener(this);

        initializeGrid();

        initializeGame();
        setFocusable(true); // GamePanel can be "focused"" to receive key input
    }

    public void startGameThread() {
        running = true;
        gameThread.start();
    }

    public void stopGameThread() {
        running = false;
    }

    private void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new JPanel();
                grid[i][j].setBackground(Color.WHITE);
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                add(grid[i][j]);
            }
        }
    }

    private void initializeGame() {
        snake.clear();
        snake.add(new Point(10, 2));
        spawnFood();
        updateGrid();
    }

    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_SIZE);
            y = rand.nextInt(GRID_SIZE);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    private void updateGame() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case UP -> newHead.x--;
            case DOWN -> newHead.x++;
            case LEFT -> newHead.y--;
            case RIGHT -> newHead.y++;
            default -> {
                return;
            }
        }

        if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE || snake.contains(newHead)) {
            stopGameThread();
            JOptionPane.showMessageDialog(this, "Game Over!");
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            spawnFood();
        } else {
            snake.removeLast();
        }

        updateGrid();
    }

    private void updateGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j].setBackground(Color.WHITE);
            }
        }

        for (Point p : snake) {
            grid[p.x][p.y].setBackground(Color.GREEN);
        }

        grid[food.x][food.y].setBackground(Color.RED);
    }

    @Override
    public void run() {
        gameLoop();
    }

    // Delta/Accumulator method
    public void gameLoop() {
        double delta = 0;
        long lastTick = System.nanoTime();
        long currentTick = 0;
        long timer = 0;
        int frameCount = 0;

        while (running) {
            currentTick = System.nanoTime();
            delta += (currentTick - lastTick) / DRAW_INTERVAL;
            timer += (currentTick - lastTick);
            lastTick = currentTick;

            if (delta >= 1) {
                updateGame();
                delta--;
                frameCount++;
            }

            if (timer >= Math.pow(10, 9)) {
                System.out.println("FPS: " + frameCount);
                timer = 0;
                frameCount = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (direction != Direction.DOWN) {
                    direction = Direction.UP;
                }
            }
            case KeyEvent.VK_DOWN -> {
                if (direction != Direction.UP) {
                    direction = Direction.DOWN;
                }
            }
            case KeyEvent.VK_LEFT -> {
                if (direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}