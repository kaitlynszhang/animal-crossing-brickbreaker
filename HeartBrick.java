import java.awt.*;
import javax.swing.*;

public class HeartBrick extends Brick {
    
    public HeartBrick(int x, int y, int width, int height) {
        super(x, y, width, height, new Color(255, 105, 180), 1); // heart requires 1 hit
        
        try {
            brickImage = new ImageIcon("HeartBrick.png");
            if (brickImage.getIconWidth() <= 0) {
                System.out.println("Warning: Failed to properly load HeartBrick.png");
            }
        } catch (Exception e) {
            System.out.println("error loading heart brick image: " + e.getMessage());
            e.printStackTrace();
            brickImage = null;
        }
    }
    
    @Override
    protected void drawHitIndicator(Graphics g) {
        // heart bricks only need 1 hit, so no indicator needed
    }
    
    @Override
    public String getFruitType() {
        return "heart";
    }
    
    @Override
    public int getPoints() {
        return 15; // hearts are worth 15 points
    }
    
    @Override
    public boolean shouldDropPowerUp() {
        return true; // always drop a heart power-up
    }
}