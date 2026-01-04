import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HowToPlayScreen extends GameScreen {
    private ImageIcon backgroundImage;
    private Rectangle gotItButton;
    private boolean showDebugBounds = false;
    
    public HowToPlayScreen(BrickBreakerGame game) {
        super(game);
        
        // load image
        try {
            backgroundImage = new ImageIcon("HowToPlayScreen.png");
        } catch (Exception e) {
            System.out.println("error loading how to play image: " + e.getMessage());
            backgroundImage = null;
        }
        
        // add got it bounds
        gotItButton = new Rectangle(215, 470, 171, 61);
        
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
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gotItButton.contains(e.getPoint())) {
                    game.playClickSound();
                    game.showScreen("menu");
                }
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
        
        // draw got it button bounds
        g.setColor(new Color(0, 255, 0, 128)); // semi-transparent green
        g.fillRect(gotItButton.x, gotItButton.y, 
                  gotItButton.width, gotItButton.height);
        g.setColor(Color.GREEN);
        g.drawRect(gotItButton.x, gotItButton.y, 
                  gotItButton.width, gotItButton.height);
        g.drawString("got it button", gotItButton.x + 5, gotItButton.y + 20);
        
        // show coordinates
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("bounds: x=" + gotItButton.x + 
                    ", y=" + gotItButton.y + 
                    ", w=" + gotItButton.width + 
                    ", h=" + gotItButton.height, 
                    20, 40);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
        
        // draw debug bounds if enabled
        if (showDebugBounds) {
            drawDebugBounds(g);
        }
    }
}