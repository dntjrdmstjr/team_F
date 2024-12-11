import java.awt.*;

public class Projectile {
    private double x, y;
    private double velocityX;
    private static final double SPEED = 15.0;  // 발사 속도
    private boolean isActive = true;
    private int size = 20;  // 발사체 크기
    private String type;    // 발사체 타입
    
    public Projectile(double x, double y, boolean facingRight, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        // 직선으로 날아가도록 수평 속도만 설정
        velocityX = facingRight ? SPEED : -SPEED;
    }
    
    public void update() {
        // 수평으로만 이동
        x += velocityX;
        
        // 화면 밖으로 나가면 비활성화
        if (x < 0 || x > GameStart.SCREEN_WIDTH) {
            isActive = false;
        }
    }
    
    public void draw(Graphics g) {
        switch(type) {
            case "FIRE":
                g.setColor(new Color(255, 100, 0));  // 밝은 주황색
                break;
            case "ICE":
                g.setColor(new Color(100, 200, 255));  // 밝은 하늘색
                break;
            case "DARK":
                g.setColor(new Color(100, 0, 100));  // 보라색
                break;
            default:
                g.setColor(Color.PINK);
        }
        g.fillOval((int)x, (int)y, size, size);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
} 