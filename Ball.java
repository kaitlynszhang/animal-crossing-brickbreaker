import java.awt.*;
import javax.swing.*;

public class Ball {
    private double x, y;           // position
    private double dx, dy;         // velocity
    private int radius;            // ball radius
    private ImageIcon ballImage;   // image for the ball
    private boolean launched;      // whether ball is moving or waiting on paddle
    
    public Ball(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.launched = false;
        
        // default starting velocity when launched
        this.dx = 2;
        this.dy = -5;
        
        try {
            ballImage = new ImageIcon("Ball.png");
            if (ballImage.getIconWidth() <= 0) {
                System.out.println("Warning: Failed to properly load Ball.png");
            }
        } catch (Exception e) {
            System.out.println("error loading ball image: " + e.getMessage());
            e.printStackTrace();
            ballImage = null;
        }
    }
    
    public void move() {
        if (launched) {
            x += dx;
            y += dy;
        }
    }
    
    public void launch() {
        launched = true;
    }
    
    public void reverseX() {
        dx = -dx;
    }
    
    public void reverseY() {
        dy = -dy;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.launched = false;
        // reset velocity
        this.dx = 2;
        this.dy = -5;
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)(x - radius), (int)(y - radius), 
                            radius * 2, radius * 2);
    }
    
    public boolean isLaunched() {
        return launched;
    }
    
    public void draw(Graphics g) {
        if (ballImage != null && ballImage.getIconWidth() > 0) {
            g.drawImage(ballImage.getImage(), (int)(x - radius), (int)(y - radius), 
                       radius * 2, radius * 2, null);
        }
    }
    
    // getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getRadius() { return radius; }
    
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
}