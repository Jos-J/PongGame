// adding Ball Class //

import java.awt.*;
import java.util.*;

public class Ball extends Rectangle {

    Random random;
    int xVelocity;
    int yVelocity;
    int initialSpeed = 4;

    Ball(int x, int y, int width, int height) {
        super(x, y, width, height);
        random = new Random();
        int randomXDirection = random.nextBoolean() ? 1 : -1;
        int randomYDirection = random.nextBoolean() ? 1 : -1;
        xVelocity = randomXDirection * initialSpeed;
        yVelocity = randomYDirection * initialSpeed;
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public void setXDirection(int x) {
        xVelocity = x;
    }

    public void setYDirection(int y) {
        yVelocity = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
