// score class //

import java.awt.*;

public class Score extends Rectangle {
    static int GAME_WIDTH;
    static int GAME_HEIGHT;
    int player = 0;
    int ai = 0;

    Score(int GAME_WIDTH, int GAME_HEIGHT) {
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 60));
        g.drawString(String.valueOf(player), GAME_WIDTH / 2 - 85, 50);
        g.drawString(String.valueOf(ai), GAME_WIDTH / 2 + 20, 50);
    }
}
