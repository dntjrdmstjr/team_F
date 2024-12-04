import java.awt.*;

public class Platform {
    private int x, y;
    private int width, height;
    
    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void draw(Graphics g) {
        g.setColor(new Color(139, 69, 19));  // 갈색
        g.fillRect(x, y, width, height);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
} 