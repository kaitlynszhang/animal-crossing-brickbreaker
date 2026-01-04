import java.awt.*;
import javax.swing.*;

public abstract class Brick {
    protected int x, y;             // position
    protected int width, height;    // dimensions
    protected Color color;          // brick color for fallback
    protected int hitsRequired;     // hits needed to break
    protected int currentHits;      // current hit count
    protected boolean broken;       // whether brick is broken
    protected ImageIcon brickImage; // image for the brick
    
    public Brick(int x, int y, int width, int height, Color color, int hitsRequired) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.hitsRequired = hitsRequired;
        this.currentHits = 0;
        this.broken = false;
    }
    
    public void hit() {
        currentHits++;
        if (currentHits >= hitsRequired) {
            broken = true;
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void draw(Graphics g) {
        if (!broken) {
            if (brickImage != null && brickImage.getIconWidth() > 0) {
                // draw the image instead of a rectangle
                g.drawImage(brickImage.getImage(), x, y, width, height, null);
            } else {
                // fallback to drawing a colored rectangle
                g.setColor(color);
                g.fillRect(x, y, width, height);
                
                // draw a border
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
                
                // draw indicator of remaining hits
                drawHitIndicator(g);
            }
        }
    }
    
    // method to be implemented by subclasses to show damage/hits
    protected abstract void drawHitIndicator(Graphics g);
    
    // check if brick should drop a power-up
    public boolean shouldDropPowerUp() {
        // 20% chance to drop a power-up when broken
        return broken && Math.random() < 0.2;
    }
    
    // method to get point value of the brick
    public int getPoints() {
        // base implementation - can be overridden by specific fruit types
        return hitsRequired * 10; // more hits = more points
    }
    
    // method to get the fruit type (for counting purposes)
    public abstract String getFruitType();
    
    // method to indicate if this brick is a penalty (like peaches)
    public boolean isPenalty() {
        // default is false, peach will override this
        return false;
    }
    
    // getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isBroken() { return broken; }
    public void setBroken(boolean broken) { this.broken = broken; }
    public Color getColor() { return color; }
    public int getHitsRequired() { return hitsRequired; }
    public int getCurrentHits() { return currentHits; }
}