
/**
 * Write a description of class GameScreen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import javax.swing.*;

public abstract class GameScreen extends JPanel {
    protected BrickBreakerGame game;
    
    public GameScreen(BrickBreakerGame game) {
        this.game = game;
        setLayout(null);
    }
    
    // this gets called when screen in active
    public void onActivate() {}
}
