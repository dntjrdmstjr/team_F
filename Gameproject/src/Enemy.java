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
    private boolean isShot = false;
    private double shotSpeed = 15.0;
    private boolean isVisible = true;
    
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
                // 거리에 따른 속도 조절
                double baseSpeed = 5.0;  // 기본 속도
                double maxSpeed = 20.0;  // 최대 속도
                double speedMultiplier = 1.0 + (distance / 50);  // 거리가 멀수록 빠르게
                
                // 가까워질수록 더 빠르게 (지수적 증가)
                if (distance < 100) {
                    speedMultiplier *= (1.0 + Math.pow((100 - distance) / 100, 2) * 3);
                }
                
                // 속도 제한
                double speed = Math.min(baseSpeed * speedMultiplier, maxSpeed);
                
                // 이동
                x += (dx / distance) * speed;
                y += (dy / distance) * speed;
                
                // 크기 축소 효과 (가까워질수록 작아짐)
                if (distance < 50) {
                    size = (int)(40 * (distance / 50));
                }
            }
        } else if (isShot) {
            // 발사체 업데이트
            x += velocityX;
            
            // 화면 밖으로 나가면 제거
            if (x < 0 || x > GameStart.SCREEN_WIDTH) {
                isAlive = false;
            }
        } else {
            // 일반 이동 로직
            if (verticalMovement) {
                y = initialY + (int)(Math.sin(angle) * verticalRange);
                angle += 0.05;
                if (angle > Math.PI * 2) angle = 0;
            } else {
                x += velocityX;
                if (x <= 0 || x >= GameStart.SCREEN_WIDTH - size) {
                    velocityX *= -1;
                }
            }
        }
    }
    
    public void draw(Graphics g) {
        if (!isAlive || !isVisible) return;
        
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
        if (isVisible) {  // 보이는 상태일 때만 죽음 처리
            isAlive = false;
            hp = 0;
        }
    }
    
    
    public void takeDamage(int damage) {
        // 흡입 중이거나 보이지 않을 때는 데미지를 받지 않음
        if (!isBeingInhaled && isVisible) {
            hp -= damage;
            if (hp <= 0) {
                isAlive = false;
            }
        }
    }
    
    public void startInhale(Kirby kirby) {
        isBeingInhaled = true;
        targetKirby = kirby;
        // 흡입 시작 시 무적 상태로
        hp = 100;  // 체력 유지
    }
    
    public void stopInhale() {
        isBeingInhaled = false;
        targetKirby = null;
    }
    
    public boolean isBeingInhaled() {
        return isBeingInhaled;
    }
    
    public void shoot(boolean facingRight, double startX, double startY) {
        isShot = true;
        isVisible = true;  // 발사할 때 다시 보이게 함
        x = startX;
        y = startY;
        
        // 발사 방향 설정
        velocityX = facingRight ? shotSpeed : -shotSpeed;
    }
    
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (!visible) {
            isAlive = false;  // 보이지 않게 될 때 죽은 상태로 변경
        }
    }
} 