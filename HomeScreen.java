import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomeScreen extends GameScreen {
    private ImageIcon backgroundImage;
    private Rectangle playButtonBounds;
    private boolean showDebugBounds = false;
    
    public HomeScreen(BrickBreakerGame game) {
        super(game);
        
        try {
            backgroundImage = new ImageIcon("StartScreen.png");
        } catch (Exception e) {
            System.out.println("error loading home background: " + e.getMessage());
            backgroundImage = null;
        }
        
        // because the play button is in the photo we are making bounds for it
        // adjusted to match the actual circular play button in bottom right
        playButtonBounds = new Rectangle(415, 440, 90, 90);
        
        // add key listener for debug mode
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    showDebugBounds = !showDebugBounds;
                    System.out.println("debug bounds: " + (showDebugBounds ? "on" : "off"));
                    repaint();
                }
            }
        });
        
        // custom mouse listener instead of the helper function
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playButtonBounds.contains(e.getPoint())) {
                    game.playClickSound();
                    game.showScreen("doorKnock");
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (playButtonBounds.contains(e.getPoint())) {
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    public void onActivate() {
        // request focus to ensure key listener works
        requestFocusInWindow();
    }
    
    private void drawDebugBounds(Graphics g) {
        // display debug information at top of screen
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("debug mode on", 20, 20);
        
        // draw play button bounds
        g.setColor(new Color(255, 0, 0, 128)); // semi-transparent red
        g.fillRect(playButtonBounds.x, playButtonBounds.y, 
                  playButtonBounds.width, playButtonBounds.height);
        g.setColor(Color.RED);
        g.drawRect(playButtonBounds.x, playButtonBounds.y, 
                  playButtonBounds.width, playButtonBounds.height);
        g.drawString("play button", playButtonBounds.x + 5, playButtonBounds.y + 20);
        
        // show coordinates
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("bounds: x=" + playButtonBounds.x + 
                    ", y=" + playButtonBounds.y + 
                    ", w=" + playButtonBounds.width + 
                    ", h=" + playButtonBounds.height, 
                    20, 40);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            // draw background
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
        
        // draw debug bounds if enabled
        if (showDebugBounds) {
            drawDebugBounds(g);
        }
    }
}