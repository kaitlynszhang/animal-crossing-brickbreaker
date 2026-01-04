import java.awt.*;
import javax.swing.*;

public class AppleBrick extends Brick {
    
    public AppleBrick(int x, int y, int width, int height) {
        super(x, y, width, height, new Color(255, 90, 90), 1); // aApple requires 1 hit
        
        try {
            brickImage = new ImageIcon("AppleBrick.png");
            if (brickImage.getIconWidth() <= 0) {
                System.out.println("Warning: Failed to properly load AppleBrick.png");
            }
        } catch (Exception e) {
            System.out.println("error loading apple brick image: " + e.getMessage());
            e.printStackTrace();
            brickImage = null;
        }
    }
    
    @Override
    protected void drawHitIndicator(Graphics g) {
        // apple bricks only need 1 hit, so no indicator needed
    }
    
    @Override
    public String getFruitType() {
        return "apple";
    }
    
    @Override
    public int getPoints() {
        return 10; // apples are worth 10 points
    }
}