// creating the GamePanel //

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 800;
    static final int GAME_HEIGHT = 600;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int PADDLE_WIDTH = 20;
    static final int PADDLE_HEIGHT = 100;
    static final int BALL_DIAMETER = 20;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Image background;
    Random random;
    Paddle player1;
    Paddle ai;
    Ball ball;
    Score score;

    GamePanel(){
        background = new ImageIcon("../assets/background1.jpg").getImage();
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread =  new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        ball = new Ball(GAME_WIDTH / 2 - BALL_DIAMETER / 2,
                    GAME_HEIGHT / 2 - BALL_DIAMETER / 2,
                    BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        player1 = new Paddle(0, GAME_HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        ai = new Paddle(GAME_WIDTH - PADDLE_WIDTH, GAME_HEIGHT / 2 - PADDLE_HEIGHT / 2,
                        PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        // adding background//
        g.drawImage(background, 0, 0, GAME_WIDTH, GAME_HEIGHT, this);
       
        //game objects//
        player1.draw(g);
        ai.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void move() {
        player1.move();
        ai.moveAI(ball); // simple at follows ball
        ball.move();
    }

    public void checkCollision() {
        //ball bounces off top & bottom//
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        // ball bounces off paddels //
        if (ball.intersects(player1) || ball.intersects(ai)) {
            playSound("../assets/hit.wav");
            ball.setXDirection(-ball.xVelocity);
        }

        // Scoring
        if(ball.x <= 0) {
            playSound("../assets/hit.wav");
            score.ai++;
            newPaddles();
            newBall();
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            playSound("../assets/score.wav");
            score.player++;
            newPaddles();
            newBall();
        }

    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            player1.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            player1.keyReleased(e);
        }
    }

    // Adding sound play back//
    public void playSound(String soundFile) {
    try {
        File file = new File("../assets/" + soundFile);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    } catch (Exception e) {
        System.out.println("Sound error: " + e.getMessage());
    }
}

}
