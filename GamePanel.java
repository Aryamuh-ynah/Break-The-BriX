/*
 * @author Humayra
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Main game panel. Handles drawing, keyboard input, movement, collision,
 * score, lives, power-ups, high score, and restart behavior.
 */
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private boolean gameOver = false;
    private boolean gameWon = false;

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

    private final HighScoreManager highScoreManager = new HighScoreManager();
    private final Random random = new Random();

    private int highScore = 0;
    private int destroyedBricks = 0;
    private int paddleWidth = GameConfig.PADDLE_WIDTH;

    private final List<PowerUp> powerUps = new ArrayList<>();
    private boolean paddlePowerUpActive = false;
    private int paddlePowerUpTimer = 0;

    public GamePanel(String name, int selectedLevel) {
        playerName = name;
        level = selectedLevel;
        highScore = highScoreManager.loadHighScore();

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
        drawScoreAndLives(g);

        if (gameWon) {
            showWinMessage(g);
            return;
        }

        if (gameOver) {
            showGameOverMessage(g);
            return;
        }

        drawPaddle(g);
        drawBall(g);
        drawPowerUps(g);
    }

    private void drawBackground(Graphics g) {
        g.setColor(GameConfig.BACKGROUND_COLOR);
        g.fillRect(0, 0, GameConfig.PANEL_WIDTH, GameConfig.PANEL_HEIGHT);
    }

    private void drawBorders(Graphics g) {
        g.setColor(GameConfig.BORDER_COLOR);
        g.fillRect(0, 0, GameConfig.BORDER_THICKNESS, GameConfig.PANEL_HEIGHT);
        g.fillRect(0, 0, GameConfig.PANEL_WIDTH, GameConfig.BORDER_THICKNESS);
        g.fillRect(GameConfig.PANEL_WIDTH - GameConfig.BORDER_THICKNESS, 0,
                GameConfig.BORDER_THICKNESS, GameConfig.PANEL_HEIGHT);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(GameConfig.PADDLE_COLOR);
        g.fillRect(paddleX, GameConfig.PADDLE_Y, paddleWidth, GameConfig.PADDLE_HEIGHT);
    }

    private void drawBall(Graphics g) {
        g.setColor(GameConfig.BALL_COLOR);
        g.fillOval(ballX, ballY, GameConfig.BALL_SIZE, GameConfig.BALL_SIZE);
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
    }

    private void drawScoreAndLives(Graphics g) {
        g.setColor(GameConfig.BORDER_COLOR);
        g.setFont(GameConfig.MEDIUM_FONT);

        g.drawString("Lives: " + lives, 20, 30);
        g.drawString("High Score: " + highScore, GameConfig.PANEL_WIDTH / 2 - 90, 30);
        g.drawString("Score: " + score, GameConfig.PANEL_WIDTH - 170, 30);
    }

    private void showWinMessage(Graphics g) {
        updateHighScore();

        g.setColor(GameConfig.WIN_MESSAGE_COLOR);

        g.setFont(GameConfig.LARGE_FONT);
        g.drawString("You Won!", GameConfig.PANEL_WIDTH / 2 - 90, 170);

        g.setFont(GameConfig.MEDIUM_FONT);
        g.drawString("Player: " + playerName, GameConfig.PANEL_WIDTH / 2 - 100, 240);
        g.drawString("Score: " + score, GameConfig.PANEL_WIDTH / 2 - 70, 300);
        g.drawString("High Score: " + highScore, GameConfig.PANEL_WIDTH / 2 - 100, 360);

        g.setFont(GameConfig.SMALL_FONT);
        g.drawString("Press Enter to Restart", GameConfig.PANEL_WIDTH / 2 - 110, 430);
        g.drawString("Press 1 Easy | 2 Medium | 3 Hard", GameConfig.PANEL_WIDTH / 2 - 140, 470);
    }

    private void showGameOverMessage(Graphics g) {
        updateHighScore();

        g.setColor(GameConfig.BORDER_COLOR);

        g.setFont(GameConfig.LARGE_FONT);
        g.drawString("Game Over!", GameConfig.PANEL_WIDTH / 2 - 110, 170);

        g.setFont(GameConfig.MEDIUM_FONT);
        g.drawString("Player: " + playerName, GameConfig.PANEL_WIDTH / 2 - 100, 240);
        g.drawString("Score: " + score, GameConfig.PANEL_WIDTH / 2 - 70, 300);
        g.drawString("High Score: " + highScore, GameConfig.PANEL_WIDTH / 2 - 100, 360);

        g.setFont(GameConfig.SMALL_FONT);
        g.drawString("Press Enter to Restart", GameConfig.PANEL_WIDTH / 2 - 110, 430);
        g.drawString("Press 1 Easy | 2 Medium | 3 Hard", GameConfig.PANEL_WIDTH / 2 - 140, 470);
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreManager.saveHighScore(highScore);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (play && !gameOver && !gameWon) {
            handlePaddleCollision();
            handleBrickCollision();
            updatePowerUps();
            updatePaddlePowerUpTimer();
            moveBall();
            handleWallCollision();
            checkMissedBall();
            checkWin();
        }

        repaint();
    }

    private void handlePaddleCollision() {
        Rectangle ballRect = new Rectangle(ballX, ballY, GameConfig.BALL_SIZE, GameConfig.BALL_SIZE);

        Rectangle paddleRect = new Rectangle(
                paddleX,
                GameConfig.PADDLE_Y,
                paddleWidth,
                GameConfig.PADDLE_HITBOX_HEIGHT
        );

        if (ballRect.intersects(paddleRect)) {
            ballYDirection = -Math.abs(ballYDirection);
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
                    brickMap.reduceBrickStrength(row, col);

                    if (brickMap.bricks[row][col] <= 0) {
                        totalBricks--;
                        destroyedBricks++;
                        score += GameConfig.POINTS_PER_BRICK;

                        maybeDropPowerUp(
                                brickX + brickMap.brickWidth / 2,
                                brickY + brickMap.brickHeight / 2
                        );

                        increaseBallSpeedIfNeeded();
                    }

                    if (ballX + GameConfig.BALL_SIZE - 1 <= brickRect.x
                            || ballX + 1 >= brickRect.x + brickRect.width) {
                        ballXDirection = -ballXDirection;
                    } else {
                        ballYDirection = -ballYDirection;
                    }

                    return;
                }
            }
        }
    }

    private void maybeDropPowerUp(int x, int y) {
        int chance = random.nextInt(100) + 1;

        if (chance <= GameConfig.POWER_UP_DROP_CHANCE_PERCENT) {
            powerUps.add(new PowerUp(x, y));
        }
    }

    private void updatePowerUps() {
        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.move();

            Rectangle paddleRect = new Rectangle(
                    paddleX,
                    GameConfig.PADDLE_Y,
                    paddleWidth,
                    GameConfig.PADDLE_HEIGHT
            );

            if (powerUp.getBounds().intersects(paddleRect)) {
                activatePaddlePowerUp();
                powerUp.deactivate();
            }

            if (!powerUp.isActive() || powerUp.isOutOfScreen()) {
                iterator.remove();
            }
        }
    }

    private void activatePaddlePowerUp() {
        paddlePowerUpActive = true;
        paddlePowerUpTimer = GameConfig.POWER_UP_DURATION_TICKS;
        paddleWidth = getPaddleWidthForLevel(level) + GameConfig.POWER_UP_PADDLE_BONUS_WIDTH;
    }

    private void updatePaddlePowerUpTimer() {
        if (!paddlePowerUpActive) {
            return;
        }

        paddlePowerUpTimer--;

        if (paddlePowerUpTimer <= 0) {
            paddlePowerUpActive = false;
            paddleWidth = getPaddleWidthForLevel(level);
        }
    }

    private void increaseBallSpeedIfNeeded() {
        if (destroyedBricks % GameConfig.SPEED_INCREASE_EVERY_N_BRICKS != 0) {
            return;
        }

        if (Math.abs(ballXDirection) < GameConfig.MAX_BALL_SPEED) {
            ballXDirection += ballXDirection > 0 ? 1 : -1;
        }

        if (Math.abs(ballYDirection) < GameConfig.MAX_BALL_SPEED) {
            ballYDirection += ballYDirection > 0 ? 1 : -1;
        }
    }

    private void moveBall() {
        ballX += ballXDirection;
        ballY += ballYDirection;
    }

    private void handleWallCollision() {
        if (ballX <= 0) {
            ballXDirection = Math.abs(ballXDirection);
        }

        if (ballY <= 0) {
            ballYDirection = Math.abs(ballYDirection);
        }

        if (ballX >= GameConfig.PANEL_WIDTH - GameConfig.BALL_SIZE) {
            ballXDirection = -Math.abs(ballXDirection);
        }
    }

    private void checkMissedBall() {
        if (ballY <= GameConfig.PANEL_HEIGHT) {
            return;
        }

        lives--;

        if (lives <= 0) {
            lives = 0;
            endGame();
        } else {
            resetBallAfterLifeLost();
        }
    }

    private void checkWin() {
        if (totalBricks <= 0) {
            gameWon = true;
            play = false;
            stopBall();
            powerUps.clear();
            updateHighScore();
        }
    }

    private void endGame() {
        gameOver = true;
        play = false;
        stopBall();
        powerUps.clear();

        /*
         * Move the ball outside the visible screen.
         * This fixes the issue where the ball remains visible after game over.
         */
        ballX = -100;
        ballY = -100;

        updateHighScore();
    }

    private void stopBall() {
        ballXDirection = 0;
        ballYDirection = 0;
    }

    private void resetBallAfterLifeLost() {
        play = false;

        powerUps.clear();
        paddlePowerUpActive = false;
        paddlePowerUpTimer = 0;

        paddleWidth = getPaddleWidthForLevel(level);
        paddleX = GameConfig.PADDLE_START_X;

        ballX = GameConfig.BALL_START_X;
        ballY = GameConfig.BALL_START_Y;

        ballXDirection = GameConfig.BALL_START_X_DIRECTION;
        ballYDirection = getStartingBallYDirection(level);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // Not needed for this game.
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_RIGHT && !gameOver && !gameWon) {
            movePaddleRight();
        }

        if (event.getKeyCode() == KeyEvent.VK_LEFT && !gameOver && !gameWon) {
            movePaddleLeft();
        }

        if (event.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            restartGame(level);
        }

        if (event.getKeyCode() == KeyEvent.VK_1 && !play) {
            restartGame(GameConfig.EASY_LEVEL);
        }

        if (event.getKeyCode() == KeyEvent.VK_2 && !play) {
            restartGame(GameConfig.MEDIUM_LEVEL);
        }

        if (event.getKeyCode() == KeyEvent.VK_3 && !play) {
            restartGame(GameConfig.HARD_LEVEL);
        }
    }

    private void movePaddleRight() {
        play = true;

        int maxPaddleX = GameConfig.PANEL_WIDTH - paddleWidth - 10;

        if (paddleX >= maxPaddleX) {
            paddleX = maxPaddleX;
        } else {
            paddleX += GameConfig.PADDLE_MOVE_AMOUNT;
        }
    }

    private void movePaddleLeft() {
        play = true;

        if (paddleX <= GameConfig.PADDLE_MIN_X) {
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
        gameOver = false;
        gameWon = false;

        score = 0;
        lives = GameConfig.STARTING_LIVES;
        destroyedBricks = 0;

        powerUps.clear();
        paddlePowerUpActive = false;
        paddlePowerUpTimer = 0;

        paddleWidth = getPaddleWidthForLevel(level);
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
                paddleWidth = GameConfig.EASY_PADDLE_WIDTH;
                break;

            case GameConfig.MEDIUM_LEVEL:
                brickMap = new BrickMap(GameConfig.MEDIUM_ROWS, GameConfig.MEDIUM_COLUMNS);
                totalBricks = GameConfig.MEDIUM_ROWS * GameConfig.MEDIUM_COLUMNS;
                ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;
                paddleWidth = GameConfig.MEDIUM_PADDLE_WIDTH;
                break;

            case GameConfig.HARD_LEVEL:
                brickMap = new BrickMap(GameConfig.HARD_ROWS, GameConfig.HARD_COLUMNS);
                totalBricks = GameConfig.HARD_ROWS * GameConfig.HARD_COLUMNS;
                ballYDirection = GameConfig.HARD_BALL_Y_DIRECTION;
                paddleWidth = GameConfig.HARD_PADDLE_WIDTH;
                break;

            default:
                level = GameConfig.EASY_LEVEL;
                brickMap = new BrickMap(GameConfig.EASY_ROWS, GameConfig.EASY_COLUMNS);
                totalBricks = GameConfig.EASY_ROWS * GameConfig.EASY_COLUMNS;
                ballYDirection = GameConfig.DEFAULT_BALL_Y_DIRECTION;
                paddleWidth = GameConfig.EASY_PADDLE_WIDTH;
                break;
        }
    }

    private int getPaddleWidthForLevel(int selectedLevel) {
        if (selectedLevel == GameConfig.HARD_LEVEL) {
            return GameConfig.HARD_PADDLE_WIDTH;
        }

        if (selectedLevel == GameConfig.MEDIUM_LEVEL) {
            return GameConfig.MEDIUM_PADDLE_WIDTH;
        }

        return GameConfig.EASY_PADDLE_WIDTH;
    }

    private int getStartingBallYDirection(int selectedLevel) {
        if (selectedLevel == GameConfig.HARD_LEVEL) {
            return GameConfig.HARD_BALL_Y_DIRECTION;
        }

        return GameConfig.DEFAULT_BALL_Y_DIRECTION;
    }
}