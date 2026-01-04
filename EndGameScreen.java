import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EndGameScreen extends GameScreen {
    private ImageIcon backgroundImage;
    private Rectangle playAgainButtonBounds;
    private Rectangle menuButtonBounds;
    private Rectangle quitButtonBounds;
    private int finalScore;
    
    // debug flag - off by default
    private boolean showDebugBounds = false;
    
    // fruit statistics
    private int applesCollected = 0;
    private int orangesCollected = 0;
    private int pearsCollected = 0;
    private int blueberriesCollected = 0;
    
    // fruit images
    private ImageIcon appleIcon;
    private ImageIcon orangeIcon;
    private ImageIcon pearIcon;
    private ImageIcon blueberryIcon;
    
    public EndGameScreen(BrickBreakerGame game) {
        super(game);
        
        try {
            backgroundImage = new ImageIcon("EndGameScreen.png");
            appleIcon = new ImageIcon("Apple.png");
            orangeIcon = new ImageIcon("Orange.png");
            pearIcon = new ImageIcon("Pear.png");
            blueberryIcon = new ImageIcon("Blueberry.png");
        } catch (Exception e) {
            System.out.println("error loading end game images: " + e.getMessage());
            e.printStackTrace();
            backgroundImage = null;
        }
        
        // define button areas with the coordinates you provided
        // top-left to bottom-right coordinates for each button
        playAgainButtonBounds = new Rectangle(145, 330, 219-145, 393-330); // replay button
        menuButtonBounds = new Rectangle(249, 326, 325-249, 396-326); // menu button
        quitButtonBounds = new Rectangle(356, 329, 429-356, 396-329); // quit button
        
        // add mouse listener for buttons
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // print the click location for debugging
                if (showDebugBounds) {
                    System.out.println("Click at: (" + e.getX() + ", " + e.getY() + ")");
                }
                
                if (playAgainButtonBounds.contains(e.getPoint())) {
                    // return to game with same mode
                    if (showDebugBounds) {
                        System.out.println("play again button clicked");
                        game.playClickSound();
                    }
                    game.showScreen("game");
                } else if (menuButtonBounds.contains(e.getPoint())) {
                    // return to menu to select a different mode
                    if (showDebugBounds) {
                        System.out.println("menu button clicked");
                        game.playClickSound();
                    }
                    game.showScreen("menu");
                } else if (quitButtonBounds.contains(e.getPoint())) {
                    // exit the game
                    if (showDebugBounds) {
                        System.out.println("quit button clicked");
                        game.playClickSound();
                    }
                    System.exit(0);
                }
            }
        });
        
        // add key listener to toggle debug view with D key
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
        setFocusable(true);
    }
    
    public void setFinalScore(int score) {
        this.finalScore = score;
        repaint();
    }
    
    public void setFruitStats(int apples, int oranges, int pears, int blueberries) {
        this.applesCollected = apples;
        this.orangesCollected = oranges;
        this.pearsCollected = pears;
        this.blueberriesCollected = blueberries;
        repaint();
    }
    
    @Override
    public void onActivate() {
        // request focus to ensure key listener works
        requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // default background if image fails to load
            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // draw score info if background image doesn't contain the text
        drawScoreInfo(g);
        
        // draw debug bounds if enabled
        if (showDebugBounds) {
            drawDebugBounds(g);
        }
    }
    
    private void drawScoreInfo(Graphics g) {
        g.setColor(Color.BLACK);
        
        // draw score - adjusted based on screenshot
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreStr = "" + finalScore;
        g.drawString(scoreStr, 311, 142);
        
        // draw each fruit count - adjusted based on screenshot
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // draw fruit icons and their counts
        // apple row
        if (appleIcon != null) {
            g.drawImage(appleIcon.getImage(), 99, 177, 25, 25, null);
        }
        g.drawString("" + applesCollected, 220, 192);
        
        // orange row
        if (orangeIcon != null) {
            g.drawImage(orangeIcon.getImage(), 99, 200, 25, 25, null);
        }
        g.drawString("" + orangesCollected, 227, 215);
        
        // pear row
        if (pearIcon != null) {
            g.drawImage(pearIcon.getImage(), 99, 223, 25, 25, null);
        }
        g.drawString("" + pearsCollected, 211, 237);
        
        // blueberry row
        if (blueberryIcon != null) {
            g.drawImage(blueberryIcon.getImage(), 99, 245, 25, 25, null);
        }
        g.drawString("" + blueberriesCollected, 242, 261);
        
        // total fruit collected
        int totalFruit = applesCollected + orangesCollected + pearsCollected + blueberriesCollected;
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("" + totalFruit, 348, 303);
    }
    
    private void drawDebugBounds(Graphics g) {
        // use different colors for each button bound for clarity
        
        // replay button - red
        g.setColor(new Color(255, 0, 0, 128)); // semi-transparent red
        g.fillRect(playAgainButtonBounds.x, playAgainButtonBounds.y, 
                  playAgainButtonBounds.width, playAgainButtonBounds.height);
        g.setColor(Color.RED);
        g.drawRect(playAgainButtonBounds.x, playAgainButtonBounds.y, 
                  playAgainButtonBounds.width, playAgainButtonBounds.height);
        g.drawString("play again", playAgainButtonBounds.x + 5, playAgainButtonBounds.y + 30);
        
        // menu button - green
        g.setColor(new Color(0, 255, 0, 128)); // semi-transparent green
        g.fillRect(menuButtonBounds.x, menuButtonBounds.y, 
                  menuButtonBounds.width, menuButtonBounds.height);
        g.setColor(Color.GREEN);
        g.drawRect(menuButtonBounds.x, menuButtonBounds.y, 
                  menuButtonBounds.width, menuButtonBounds.height);
        g.drawString("menu", menuButtonBounds.x + 10, menuButtonBounds.y + 30);
        
        // quit button - blue
        g.setColor(new Color(0, 0, 255, 128)); // semi-transparent blue
        g.fillRect(quitButtonBounds.x, quitButtonBounds.y, 
                  quitButtonBounds.width, quitButtonBounds.height);
        g.setColor(Color.BLUE);
        g.drawRect(quitButtonBounds.x, quitButtonBounds.y, 
                  quitButtonBounds.width, quitButtonBounds.height);
        g.drawString("quit", quitButtonBounds.x + 15, quitButtonBounds.y + 30);
        
        // draw coordinate info for all elements
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("debug mode on", 20, 20);
        
        // highlight score and text areas (using the exact final coordinates)
        g.setColor(new Color(0, 0, 255, 50));  // very transparent blue
        
        // highlight score area
        g.fillRect(311, 142-20, 50, 25);  // score area (adjust up by text height)
        
        // highlight fruit count areas
        g.fillRect(220, 192-20, 30, 25);  // apple count
        g.fillRect(227, 215-20, 30, 25);  // orange count
        g.fillRect(211, 237-20, 30, 25);  // pear count
        g.fillRect(242, 261-20, 30, 25);  // blueberry count
        g.fillRect(348, 303-20, 30, 25);  // total count
        
        // highlight fruit icon areas
        g.setColor(new Color(255, 0, 0, 50));  // very transparent red
        g.fillRect(99, 177, 25, 25);  // apple icon
        g.fillRect(99, 200, 25, 25);  // orange icon
        g.fillRect(99, 223, 25, 25);  // pear icon
        g.fillRect(99, 245, 25, 25);  // blueberry icon
    }
}