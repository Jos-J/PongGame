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

    boolean gameOver = false;
    boolean paused = false;
    final int WIN_SCORE = 5;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Image background;
    Random random;
    Paddle player1;
    Paddle ai;
    Ball ball;
    Score score;
    Clip backgroundMusic;

    GamePanel() {
        background = new ImageIcon("../assets/background1.jpg").getImage();
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        playBackgroundMusic("soundtrack.wav");

        gameThread = new Thread(this);
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
        // draw background
        g.drawImage(background, 0, 0, GAME_WIDTH, GAME_HEIGHT, this);

        // draw game objects
        player1.draw(g);
        ai.draw(g);
        ball.draw(g);
        score.draw(g);

        // show pause or game over messages
        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("PAUSED", GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2);
        }
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String winner = score.player >= WIN_SCORE ? "PLAYER WINS!" : "AI WINS!";
            g.drawString(winner, GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press 'R' to Restart", GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 40);
        }
    }

    public void move() {
        player1.move();
        ai.moveAI(ball);
        ball.move();
    }

    public void checkCollision() {
        // ball bounces off top & bottom
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        // ball bounces off paddles
        if (ball.intersects(player1) || ball.intersects(ai)) {
            playSound("hit.wav");
            ball.setXDirection(-ball.xVelocity);
        }

        // scoring
        if (ball.x <= 0) {
            playSound("hit.wav");
            score.ai++;
            checkWin();
            if (!gameOver) {
                newPaddles();
                newBall();
            }
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            playSound("score.wav");
            score.player++;
            checkWin();
            if (!gameOver) {
                newPaddles();
                newBall();
            }
        }
    }

    public void checkWin() {
        if (score.player >= WIN_SCORE) {
            System.out.println("🎉 Player Wins!");
            gameOver = true;
            stopBackgroundMusic();
        } else if (score.ai >= WIN_SCORE) {
            System.out.println("💻 AI Wins!");
            gameOver = true;
            stopBackgroundMusic();
        }
    }

    public void resetGame() {
        score.player = 0;
        score.ai = 0;
        gameOver = false;
        paused = false;
        newPaddles();
        newBall();
        playBackgroundMusic("soundtrack.wav");
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
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
                if (!paused && !gameOver) {
                    move();
                    checkCollision();
                }
                repaint();
                delta--;
            }
        }
    }

    // KeyListener inner class
    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player1.keyPressed(e);

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                paused = !paused;
                System.out.println(paused ? "⏸️ Paused" : "▶️ Resumed");
            }

            if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                resetGame();
                System.out.println("🔄 Game Reset!");
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player1.keyReleased(e);
        }
    }

    // Play short sound effects (hit, score)
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

    // Play background music (looped)
    public void playBackgroundMusic(String musicFile) {
        try {
            // stop & close existing music before starting new one
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }

            File file = new File("../assets/" + musicFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Music error: " + e.getMessage());
        }
    }
}
