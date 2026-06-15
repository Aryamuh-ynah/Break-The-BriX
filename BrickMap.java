/*
 * @author Humayra
 */
import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 * Handles brick creation, drawing, and updating brick values.
 */
public class BrickMap {
    public int[][] bricks;
    public int brickWidth;
    public int brickHeight;

    public BrickMap(int row, int col) {
        bricks = new int[row][col];

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                bricks[r][c] = 1;
            }
        }

        brickWidth = GameConfig.BRICK_AREA_WIDTH / col;
        brickHeight = GameConfig.BRICK_AREA_HEIGHT / row;
    }

    public void draw(Graphics2D g) {
        int totalRows = bricks.length;

        for (int row = 0; row < bricks.length; row++) {
            for (int col = 0; col < bricks[0].length; col++) {
                if (bricks[row][col] <= 0) {
                    continue;
                }

                if (row < totalRows / 2) {
                    g.setColor(GameConfig.TOP_BRICK_COLOR);
                } else {
                    g.setColor(GameConfig.BOTTOM_BRICK_COLOR);
                }

                int brickX = col * brickWidth + GameConfig.BRICK_START_X;
                int brickY = row * brickHeight + GameConfig.BRICK_START_Y;

                g.fillRect(brickX, brickY, brickWidth, brickHeight);

                g.setStroke(new BasicStroke(GameConfig.BRICK_BORDER_STROKE));
                g.setColor(GameConfig.BORDER_COLOR);
                g.drawRect(brickX, brickY, brickWidth, brickHeight);
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        bricks[row][col] = value;
    }
}
