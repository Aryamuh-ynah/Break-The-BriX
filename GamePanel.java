/*
 * @author Humayra
 */
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Main game panel. Handles drawing, keyboard input, movement, collision,
 * score, lives, and restart behavior.
 */
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 0;
    private int lives = GameConfig.STARTING_LIVES;
    private int level = GameConfig.EASY_LEVEL;

    private final Timer timer;

    private int paddleX = GameConfig.PADDLE_START_X;
    private int ballX = GameConfig.BALL_START_X;
    private int ballY = GameConfig.BALL_START_Y;
    private int ballXDirection = GameConfig.BALL_START_X_DIRECTION;
    private int ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;

    private BrickMap brickMap;
    private final String playerName;

    public GamePanel(String name, int selectedLevel) {
        playerName = name;
        level = selectedLevel;

        setupLevel(level);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(GameConfig.TIMER_DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);
        brickMap.draw((Graphics2D) g);
        drawBorders(g);
        drawPaddle(g);
        drawBall(g);
        drawScoreAndLives(g);

        if (totalBricks <= 0) {
            showWinMessage(g);
        }

        if (ballY > GameConfig.BALL_MISSED_Y) {
            handleMissedBall(g);
        }
    }

    private void drawBackground(Graphics g) {
        g.setColor(GameConfig.BACKGROUND_COLOR);
        g.fillRect(1, 1, GameConfig.PANEL_WIDTH, GameConfig.PANEL_HEIGHT);
    }

    private void drawBorders(Graphics g) {
        g.setColor(GameConfig.BORDER_COLOR);
        g.fillRect(0, 0, GameConfig.BORDER_THICKNESS, GameConfig.PANEL_HEIGHT);
        g.fillRect(0, 0, GameConfig.PANEL_WIDTH, GameConfig.BORDER_THICKNESS);
        g.fillRect(GameConfig.RIGHT_BORDER_X, 0, GameConfig.BORDER_THICKNESS, GameConfig.PANEL_HEIGHT);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(GameConfig.PADDLE_COLOR);
        g.fillRect(paddleX, GameConfig.PADDLE_Y, GameConfig.PADDLE_WIDTH, GameConfig.PADDLE_HEIGHT);
    }

    private void drawBall(Graphics g) {
        g.setColor(GameConfig.BALL_COLOR);
        g.fillOval(ballX, ballY, GameConfig.BALL_SIZE, GameConfig.BALL_SIZE);
    }

    private void drawScoreAndLives(Graphics g) {
        g.setColor(GameConfig.BORDER_COLOR);
        g.setFont(GameConfig.MEDIUM_FONT);
        g.drawString("Score: " + score, GameConfig.SCORE_X, GameConfig.SCORE_Y);
        g.drawString("Lives: " + lives, GameConfig.LIVES_X, GameConfig.LIVES_Y);
    }

    private void showWinMessage(Graphics g) {
        play = false;
        stopBall();

        g.setColor(GameConfig.WIN_MESSAGE_COLOR);
        g.setFont(GameConfig.LARGE_FONT);
        g.drawString("You Won, Score: " + score, GameConfig.MESSAGE_X, GameConfig.MESSAGE_Y);

        g.setFont(GameConfig.SMALL_FONT);
        g.drawString("Player: " + playerName, GameConfig.PLAYER_X, GameConfig.PLAYER_Y);
        g.drawString("Score: " + score, GameConfig.FINAL_SCORE_X, GameConfig.FINAL_SCORE_Y);
        g.drawString("Press Enter to Restart.", GameConfig.RESTART_X, GameConfig.RESTART_Y);
    }

    private void showGameOverMessage(Graphics g) {
        g.setColor(GameConfig.BORDER_COLOR);
        g.setFont(GameConfig.LARGE_FONT);
        g.drawString("Game Over!", GameConfig.MESSAGE_X, GameConfig.MESSAGE_Y);

        g.setFont(GameConfig.MEDIUM_FONT);
        g.drawString("Player: " + playerName, GameConfig.PLAYER_X, GameConfig.PLAYER_Y);
        g.drawString("Score: " + score, GameConfig.FINAL_SCORE_X, GameConfig.FINAL_SCORE_Y);
        g.drawString("Press Enter to Restart", GameConfig.RESTART_X, GameConfig.RESTART_Y);
    }

    private void handleMissedBall(Graphics g) {
        play = false;
        stopBall();

        if (lives > 0) {
            lives--;
        }

        if (lives <= 0) {
            showGameOverMessage(g);
        } else {
            resetBallAfterLifeLost();
            play = true;
        }
    }

    private void stopBall() {
        ballXDirection = 0;
        ballYDirection = 0;
    }

    private void resetBallAfterLifeLost() {
        ballX = GameConfig.BALL_RESET_X;
        ballY = GameConfig.BALL_RESET_Y;
        ballXDirection = GameConfig.BALL_START_X_DIRECTION;
        ballYDirection = getStartingBallYDirection(level);
        paddleX = GameConfig.PADDLE_START_X;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (play) {
            handlePaddleCollision();
            handleBrickCollision();
            moveBall();
            handleWallCollision();
        }

        repaint();
    }

    private void handlePaddleCollision() {
        Rectangle ballRect = new Rectangle(ballX, ballY, GameConfig.BALL_SIZE, GameConfig.BALL_SIZE);
        Rectangle paddleRect = new Rectangle(
                paddleX,
                GameConfig.PADDLE_Y,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HITBOX_HEIGHT
        );

        if (ballRect.intersects(paddleRect)) {
            ballYDirection = -ballYDirection;
        }
    }

    private void handleBrickCollision() {
        for (int row = 0; row < brickMap.bricks.length; row++) {
            for (int col = 0; col < brickMap.bricks[0].length; col++) {
                if (brickMap.bricks[row][col] <= 0) {
                    continue;
                }

                int brickX = col * brickMap.brickWidth + GameConfig.BRICK_START_X;
                int brickY = row * brickMap.brickHeight + GameConfig.BRICK_START_Y;
                Rectangle brickRect = new Rectangle(brickX, brickY, brickMap.brickWidth, brickMap.brickHeight);
                Rectangle ballRect = new Rectangle(ballX, ballY, GameConfig.BALL_SIZE, GameConfig.BALL_SIZE);

                if (ballRect.intersects(brickRect)) {
                    brickMap.setBrickValue(0, row, col);
                    totalBricks--;
                    score += GameConfig.POINTS_PER_BRICK;

                    if (ballX + GameConfig.BALL_SIZE - 1 <= brickRect.x || ballX + 1 >= brickRect.x + brickRect.width) {
                        ballXDirection = -ballXDirection;
                    } else {
                        ballYDirection = -ballYDirection;
                    }

                    return;
                }
            }
        }
    }

    private void moveBall() {
        ballX += ballXDirection;
        ballY += ballYDirection;
    }

    private void handleWallCollision() {
        if (ballX < 0) {
            ballXDirection = -ballXDirection;
        }

        if (ballY < 0) {
            ballYDirection = -ballYDirection;
        }

        if (ballX > GameConfig.BALL_MAX_X) {
            ballXDirection = -ballXDirection;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // Not needed for this game.
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            movePaddleRight();
        }

        if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            movePaddleLeft();
        }

        if (event.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            restartGame(level);
        }

        if (event.getKeyCode() == KeyEvent.VK_1 && !play) {
            level = GameConfig.EASY_LEVEL;
            restartGame(level);
        } else if (event.getKeyCode() == KeyEvent.VK_2 && !play) {
            level = GameConfig.MEDIUM_LEVEL;
            restartGame(level);
        } else if (event.getKeyCode() == KeyEvent.VK_3 && !play) {
            level = GameConfig.HARD_LEVEL;
            restartGame(level);
        }
    }

    private void movePaddleRight() {
        play = true;

        if (paddleX >= GameConfig.PADDLE_MAX_X) {
            paddleX = GameConfig.PADDLE_MAX_X;
        } else {
            paddleX += GameConfig.PADDLE_MOVE_AMOUNT;
        }
    }

    private void movePaddleLeft() {
        play = true;

        if (paddleX < GameConfig.PADDLE_MIN_X) {
            paddleX = GameConfig.PADDLE_MIN_X;
        } else {
            paddleX -= GameConfig.PADDLE_MOVE_AMOUNT;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        // Not needed for this game.
    }

    private void restartGame(int selectedLevel) {
        level = selectedLevel;
        play = true;
        score = 0;
        lives = GameConfig.STARTING_LIVES;
        paddleX = GameConfig.PADDLE_START_X;
        ballX = GameConfig.BALL_START_X;
        ballY = GameConfig.BALL_START_Y;
        ballXDirection = GameConfig.BALL_START_X_DIRECTION;
        ballYDirection = getStartingBallYDirection(level);

        setupLevel(level);
        repaint();
    }

    private void setupLevel(int selectedLevel) {
        switch (selectedLevel) {
            case GameConfig.EASY_LEVEL:
                brickMap = new BrickMap(GameConfig.EASY_ROWS, GameConfig.EASY_COLUMNS);
                totalBricks = GameConfig.EASY_ROWS * GameConfig.EASY_COLUMNS;
                ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;
                break;
            case GameConfig.MEDIUM_LEVEL:
                brickMap = new BrickMap(GameConfig.MEDIUM_ROWS, GameConfig.MEDIUM_COLUMNS);
                totalBricks = GameConfig.MEDIUM_ROWS * GameConfig.MEDIUM_COLUMNS;
                ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;
                break;
            case GameConfig.HARD_LEVEL:
                brickMap = new BrickMap(GameConfig.HARD_ROWS, GameConfig.HARD_COLUMNS);
                totalBricks = GameConfig.HARD_ROWS * GameConfig.HARD_COLUMNS;
                ballYDirection = GameConfig.HARD_BALL_Y_DIRECTION;
                break;
            default:
                brickMap = new BrickMap(GameConfig.EASY_ROWS, GameConfig.EASY_COLUMNS);
                totalBricks = GameConfig.EASY_ROWS * GameConfig.EASY_COLUMNS;
                ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;
                level = GameConfig.EASY_LEVEL;
                break;
        }
    }

    private int getStartingBallYDirection(int selectedLevel) {
        if (selectedLevel == GameConfig.HARD_LEVEL) {
            return GameConfig.HARD_BALL_Y_DIRECTION;
        }

        return GameConfig.DEFAULT_BALL_Y_DIRECTION;
    }
}
