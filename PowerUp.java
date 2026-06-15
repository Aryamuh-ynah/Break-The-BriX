import java.awt.Graphics;
import java.awt.Rectangle;

public class PowerUp {
    private int x;
    private int y;
    private boolean active;

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }

    public void draw(Graphics g) {
        if (!active) {
            return;
        }

        g.setColor(GameConfig.POWER_UP_COLOR);
        g.fillOval(x, y, GameConfig.POWER_UP_SIZE, GameConfig.POWER_UP_SIZE);
    }

    public void move() {
        if (active) {
            y += GameConfig.POWER_UP_FALL_SPEED;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.POWER_UP_SIZE, GameConfig.POWER_UP_SIZE);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isOutOfScreen() {
        return y > GameConfig.PANEL_HEIGHT;
    }
}