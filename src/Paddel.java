// Paddel Class//

import java.awt.*;
import java.awt.event.*;

public class Paddle extends Rectangle {
    int id;
    int yVelocity;
    int speed = 10;

    Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id) {
        super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.id = id;
    }

    public void keyPressed(KeyEvent e) {
        if (id == 1) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                setYDirection(-speed);
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                setYDirection(speed);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (id == 1) {
            if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
                setYDirection(0);
            }
        }
    }

    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    public void move() {
        y += yVelocity;
        if (y < 0) y = 0;
        if (y > GamePanel.GAME_HEIGHT - height) y = GamePanel.GAME_HEIGHT - height;
    }

    public void moveAI(Ball ball) {
        // Basic AI: follow the ball
        if (ball.y < y) {
            y -= speed;
        } else if (ball.y > y + height) {
            y += speed;
        }
        if (y < 0) y = 0;
        if (y > GamePanel.GAME_HEIGHT - height) y = GamePanel.GAME_HEIGHT - height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }
}
