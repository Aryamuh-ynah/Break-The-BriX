/*
 * @author Humayra
 */
import java.awt.Color;
import java.awt.Font;

/**
 * Stores the fixed configuration values used by the game.
 * Keeping them here makes the code easier to read and update.
 */
public final class GameConfig {
    private GameConfig() {
        // Utility class: do not create objects from this class.
    }

    public static final int WINDOW_X = 20;
    public static final int WINDOW_Y = 10;
    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 900;

    public static final int PANEL_WIDTH = 692;
    public static final int PANEL_HEIGHT = 900;
    public static final int RIGHT_BORDER_X = 691;
    public static final int BORDER_THICKNESS = 3;

    public static final int TIMER_DELAY = 8;

    public static final int PADDLE_START_X = 310;
    public static final int PADDLE_Y = 850;
    public static final int PADDLE_WIDTH = 100;
    public static final int PADDLE_HEIGHT = 12;
    public static final int PADDLE_HITBOX_HEIGHT = 8;
    public static final int PADDLE_MOVE_AMOUNT = 40;
    public static final int PADDLE_MIN_X = 10;
    public static final int PADDLE_MAX_X = 600;

    public static final int BALL_START_X = 320;
    public static final int BALL_START_Y = 350;
    public static final int BALL_RESET_X = 120;
    public static final int BALL_RESET_Y = 350;
    public static final int BALL_SIZE = 20;
    public static final int BALL_MAX_X = 670;
    public static final int BALL_MISSED_Y = 570;
    public static final int BALL_START_X_DIRECTION = -1;
    public static final int DEFAULT_BALL_Y_DIRECTION = -3;
    public static final int HARD_BALL_Y_DIRECTION = -4;

    public static final int STARTING_LIVES = 3;
    public static final int POINTS_PER_BRICK = 3;

    public static final int EASY_LEVEL = 1;
    public static final int MEDIUM_LEVEL = 2;
    public static final int HARD_LEVEL = 3;

    public static final int EASY_ROWS = 4;
    public static final int EASY_COLUMNS = 7;
    public static final int MEDIUM_ROWS = 6;
    public static final int MEDIUM_COLUMNS = 7;
    public static final int HARD_ROWS = 8;
    public static final int HARD_COLUMNS = 8;

    public static final int BRICK_AREA_WIDTH = 540;
    public static final int BRICK_AREA_HEIGHT = 200;
    public static final int BRICK_START_X = 80;
    public static final int BRICK_START_Y = 50;
    public static final int BRICK_BORDER_STROKE = 4;

    public static final int SCORE_X = 520;
    public static final int SCORE_Y = 30;
    public static final int LIVES_X = 20;
    public static final int LIVES_Y = 30;

    public static final int MESSAGE_X = 190;
    public static final int MESSAGE_Y = 300;
    public static final int PLAYER_X = 230;
    public static final int PLAYER_Y = 350;
    public static final int FINAL_SCORE_X = 230;
    public static final int FINAL_SCORE_Y = 400;
    public static final int RESTART_X = 190;
    public static final int RESTART_Y = 450;

    public static final Font LARGE_FONT = new Font("MV Boli", Font.BOLD, 30);
    public static final Font MEDIUM_FONT = new Font("MV Boli", Font.BOLD, 25);
    public static final Font SMALL_FONT = new Font("MV Boli", Font.BOLD, 20);

    public static final Color BACKGROUND_COLOR = Color.CYAN;
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final Color PADDLE_COLOR = new Color(0x0000ff);
    public static final Color BALL_COLOR = Color.RED;
    public static final Color TOP_BRICK_COLOR = new Color(0x7f00ff);
    public static final Color BOTTOM_BRICK_COLOR = new Color(0xff33ff);
    public static final Color WIN_MESSAGE_COLOR = new Color(0xff6464);
    

    // High score
    public static final String HIGH_SCORE_FILE = "highscore.txt";

    // Difficulty-based paddle sizes
    public static final int EASY_PADDLE_WIDTH = 120;
    public static final int MEDIUM_PADDLE_WIDTH = 100;
    public static final int HARD_PADDLE_WIDTH = 80;

    // Ball speed increase
    public static final int SPEED_INCREASE_EVERY_N_BRICKS = 5;
    public static final int MAX_BALL_SPEED = 7;

    // Brick strength values
    public static final int WEAK_BRICK_STRENGTH = 1;
    public static final int MEDIUM_BRICK_STRENGTH = 2;
    public static final int STRONG_BRICK_STRENGTH = 3;

    // Power-up settings
    public static final int POWER_UP_SIZE = 20;
    public static final int POWER_UP_FALL_SPEED = 2;
    public static final int POWER_UP_DURATION_TICKS = 600;
    public static final int POWER_UP_DROP_CHANCE_PERCENT = 25;
    public static final int POWER_UP_PADDLE_BONUS_WIDTH = 40;

    public static final Color WEAK_BRICK_COLOR = new Color(0x66ccff);
    public static final Color MEDIUM_BRICK_COLOR = new Color(0xffcc00);
    public static final Color STRONG_BRICK_COLOR = new Color(0xff6666);
    public static final Color POWER_UP_COLOR = new Color(0x33cc33);

}
