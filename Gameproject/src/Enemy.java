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
    private boolean isBeingInhaled = false;  // 흡입 당하는 중인지 여부
    private Kirby targetKirby;              // 흡입하는 커비 참조
    
    public Enemy(int x, int y, String type) {
    	this(x, y, type, false);
    	
    	if(type.equals("DARK")) {
    		this.verticalMovement = true;
    		this.velocityX=3;
    		this.verticalRange=150;
    	}
    }
    public int getX() {
        return (int) x; // 적의 X 좌표를 반환
    }

    public int getY() {
        return (int) y; // 적의 Y 좌표를 반환
    }

    
    public Enemy(int x, int y, String type, boolean isVertical) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.verticalMovement = isVertical;
        this.initialY = y;
    }
    
    public void update() {
        if (!isAlive) return;
        
        if (isBeingInhaled && targetKirby != null) {
            // 커비 방향으로 이동
            double targetX = targetKirby.getX();
            double targetY = targetKirby.getY();
            
            // 커비의 입 위치로 정확하게 조정
            if (targetKirby.isFacingRight()) {
                targetX = targetX + targetKirby.getSize() - size/2;  // 입 위치로 중앙 정렬
                targetY = targetY + targetKirby.getSize()/2 - size/2;
            } else {
                targetX = targetX - size/2;  // 왼쪽 입 위치로 중앙 정렬
                targetY = targetY + targetKirby.getSize()/2 - size/2;
            }
            
            // 커비 방향으로 빨려들어가는 효과
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance > 5) {
                double speed = 8.0;  // 기본 속도를 낮춤
                // 거리가 가까워질수록 더 빠르게
                double speedMultiplier = 1.0;
                if (distance < 150) {
                    speedMultiplier = 2.0 + (150 - distance) / 30;  // 가까워질수록 가속
                }
                
                x += (dx / distance) * speed * speedMultiplier;
                y += (dy / distance) * speed * speedMultiplier;
            }
        } else {
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
    
    public void startInhale(Kirby kirby) {
        isBeingInhaled = true;
        targetKirby = kirby;
    }
    
    public void stopInhale() {
        isBeingInhaled = false;
        targetKirby = null;
    }
    
    public boolean isBeingInhaled() {
        return isBeingInhaled;
    }
} 