import java.awt.*;

public class Enemy {
    private double x, y;
    private double velocityX = 2;
    private int hp = 100;
    private int size = 40;
    private String type;  // 적의 타입 (능력을 결정)
    private boolean isAlive = true;
    private boolean verticalMovement = false;  // 수직 이동 여부
    private int verticalRange = 100;          // 수직 이동 범위
    private int initialY;                     // 초기 Y 위치
    private double angle=0;
    
    public Enemy(int x, int y, String type) {
    	this(x, y, type, false);
    	
    	if(type.equals("DARK")) {
    		this.verticalMovement = true;
    		this.velocityX=3;
    		this.verticalRange=150;
    	}
    }
    
    public Enemy(int x, int y, String type, boolean verticlaMovement) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.verticalMovement=verticalMovement;
        this.initialY = y;
    }
    
    public void update() {
        if (!isAlive) return;
        if (verticalMovement) {
            y = initialY + (int)(Math.sin(angle) * verticalRange);  // 사인파 이동
            angle += 0.05;  // 각도 증가
            if (angle > Math.PI * 2) angle = 0;  // 각도 초기화
        } else {
            x += velocityX;
            if (x <= 0 || x >= GameStart.SCREEN_WIDTH - size) {
                velocityX *= -1;  // 좌우 반전
            }
        }
    

        if (verticalMovement) {
            y += velocityX;  // velocityX를 Y 방향 속도로 사용
            if (y <= initialY - verticalRange || y >= initialY + verticalRange) {
                velocityX *= -1;  // 위아래 반전
            }
        } else {
            x += velocityX;
            if (x <= 0 || x >= GameStart.SCREEN_WIDTH - size) {
                velocityX *= -1;  // 좌우 반전
            }
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
            case "DARK":
            	g.setColor(Color.BLACK);
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