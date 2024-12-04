import java.awt.*;

public class Enemy {
    private double x, y;
    private double velocityX = 2;
    private int hp = 100;
    private int size = 40;
    private String type;  // 적의 타입 (능력을 결정)
    private boolean isAlive = true;
    
    public Enemy(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void update() {
        // 좌우로 움직이는 패턴
        x += velocityX;
        if (x <= 0 || x >= GameStart.SCREEN_WIDTH - size) {
            velocityX *= -1;
        }
    }
    
    public void draw(Graphics g) {
        if (!isAlive) return;
        
        switch(type) {
            case "FIRE":
                g.setColor(Color.RED);
                break;
            case "ICE":
                g.setColor(Color.CYAN);
                break;
            default:
                g.setColor(Color.GRAY);
        }
        g.fillRect((int)x, (int)y, size, size);
        
        // HP 바 추가
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 10, (int)(size * (hp/100.0)), 5);
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public void defeat() {
        isAlive = false;
    }
    
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            isAlive = false;
        }
    }
} 