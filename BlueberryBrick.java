import java.awt.*;
import javax.swing.*;

public class BlueberryBrick extends Brick {
    
    public BlueberryBrick(int x, int y, int width, int height) {
        super(x, y, width, height, new Color(70, 70, 220), 3); // blueberry requires 3 hits
        
        try {
            brickImage = new ImageIcon("BlueberryBrick.png");
            if (brickImage.getIconWidth() <= 0) {
                System.out.println("Warning: Failed to properly load BlueberryBrick.png");
            }
        } catch (Exception e) {
            System.out.println("error loading blueberry brick image: " + e.getMessage());
            e.printStackTrace();
            brickImage = null;
        }
    }
    
    @Override
    protected void drawHitIndicator(Graphics g) {
        // show cracks based on number of hits
        if (currentHits > 0) {
            g.setColor(new Color(50, 50, 180));
            
            // first hit - one crack
            g.drawLine(x + 5, y + height/2, x + width - 5, y + height/2);
            
            // second hit - cross crack
            if (currentHits > 1) {
                g.drawLine(x + width/2, y + 5, x + width/2, y + height - 5);
            }
        }
    }
    
    @Override
    public String getFruitType() {
        return "blueberry";
    }
    
    @Override
    public int getPoints() {
        return 30; // blueberries are worth 30 points
    }
}