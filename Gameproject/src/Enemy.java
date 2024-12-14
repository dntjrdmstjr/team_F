import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.AffineTransform;


public class Enemy {
    private double x, y;
    private double velocityX = 2;
    private double velocityY =0;
    private int hp = 100;
    private int size = 40;
    private String type;  // 적의 타입 (능력을 결정)
    private boolean isAlive = true;
    private boolean verticalMovement = false;  // 수직 이동 여부
    private int verticalRange = 100;          // 수직 이동 범위
    private int initialY;                     // 초기 Y 위치
    private boolean isFacingRight = true;
    private double angle=0;
    private boolean isBeingInhaled = false;  // 흡입 당하는 중인지 여부
    private Kirby targetKirby;              // 흡입하는 커비 참조
    private boolean isShot = false;
    private double shotSpeed = 15.0;
    private boolean isVisible = true;
    private BufferedImage spriteSheet;      // 스프라이트 시트 이미지
    private List<BufferedImage> sprites;   // 스프라이트 리스트 (애니메이션 프레임)
    private int spriteWidth, spriteHeight; // 스프라이트 크기
    private int animationIndex = 0;        // 현재 애니메이션 프레임
    private int animationSpeed = 100;      // 애니메이션 속도 (밀리초)
    private long lastFrameTime;
    private Platform currentPlatform;
    
    private static class SpriteInfo {
        public int x, y, width, height;

        public SpriteInfo(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    public Enemy(double x, double y, String type, Platform platform) {
    	this.x = x;
        this.y = y;
        this.type = type;
        this.currentPlatform = platform; // 적이 움직일 플랫폼 저장
        this.isFacingRight = true;
    	
        try {
            initializeSpriteSheet(type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.lastFrameTime = System.currentTimeMillis();
    }
    public Enemy(double x, double y, String type) {
        this(x, y, type, null); // 플랫폼 없이 생성
    }
    private SpriteInfo[] fireSpriteInfo = {
    	    new SpriteInfo(0, 84, 28, 36), // FIRE 스프라이트 1
    	    new SpriteInfo(30, 84, 28, 36), // FIRE 스프라이트 2
    	    new SpriteInfo(62, 84, 28, 36), // FIRE 스프라이트 3
    	    new SpriteInfo(92, 84, 28, 36), // FIRE 스프라이트 1
    	    new SpriteInfo(122, 84, 28, 36), // FIRE 스프라이트 2
    	    new SpriteInfo(150, 84, 28, 36), // FIRE 스프라이트 3
    	    new SpriteInfo(179, 84, 31, 36), // FIRE 스프라이트 3
    	};

    	private SpriteInfo[] iceSpriteInfo = {
    	    new SpriteInfo(4, 120, 38, 40), // ICE 스프라이트 1
    	    new SpriteInfo(42, 120, 34, 40), // ICE 스프라이트 2
    	    new SpriteInfo(80, 120, 38, 40), // ICE 스프라이트 3
    	    new SpriteInfo(118, 120, 36, 40), // ICE 스프라이트 2
    	    new SpriteInfo(156, 120, 34, 40), // ICE 스프라이트 3
    	
    	};

    	private SpriteInfo[] darkSpriteInfo = {
    	    new SpriteInfo(0, 392, 68, 60), // DARK 스프라이트 1
    	    new SpriteInfo(68, 392, 68, 60), // DARK 스프라이트 2
    	    new SpriteInfo(136, 392, 68, 60), // DARK 스프라이트 3
    	    new SpriteInfo(204, 392, 68, 60), // DARK 스프라이트 3
    	};

    public int getX() {
        return (int) x; // 적의 X 좌표를 반환
    }

    public int getY() {
        return (int) y; // 적의 Y 좌표를 반환
    }
    private void initializeSpriteSheet(String type) throws IOException {
        // 스프라이트 시트 로드
        spriteSheet = ImageIO.read(new File("img/enemies-sheet-alpha.png"));
        sprites = new ArrayList<>();

        // 타입에 따른 스프라이트 추출
        SpriteInfo[] selectedInfo;
        switch (type) {
            case "FIRE":
            	selectedInfo = fireSpriteInfo;
                break;

            case "ICE":
            	selectedInfo = iceSpriteInfo;
                break;

            case "DARK":
            	selectedInfo = darkSpriteInfo;
                break;
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
        for (SpriteInfo info : selectedInfo) {
            BufferedImage sprite = spriteSheet.getSubimage(info.x, info.y, info.width, info.height);
            sprites.add(sprite);
        }
    }
    private void loadSprites(int row) {
        int cols = spriteSheet.getWidth() / spriteWidth; // 스프라이트 열(column) 개수 계산
        for (int col = 0; col < cols; col++) {
            BufferedImage sprite = spriteSheet.getSubimage(
                col * spriteWidth,    // X 좌표
                row * spriteHeight,   // Y 좌표
                spriteWidth,          // 너비
                spriteHeight          // 높이
            );
            sprites.add(sprite);
        }
    }

    
    public Enemy(int x, int y, String type, boolean isVertical) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.verticalMovement = isVertical;
        this.initialY = y;
    }
    
    public void update() {
    	if (!isAlive || !isVisible || sprites == null || sprites.isEmpty()) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= animationSpeed) {
            animationIndex = (animationIndex + 1) % sprites.size(); // 다음 프레임
            lastFrameTime = currentTime;
        }
        
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
        	
        	switch (type) {
            case "DARK":
                // DARK는 자유롭게 이동
                x += velocityX;
                y += velocityY;  // 필요 시 수직 이동 추가 가능
                
                if (velocityX > 0) {
                    isFacingRight = true;  // 오른쪽 이동
                } else if (velocityX < 0) {
                    isFacingRight = false; // 왼쪽 이동
                }
                if (x <= 0) {
                    x = 0;  // 화면 왼쪽 경계로 위치 고정
                    velocityX *= -1;  // 방향 반전
                } else if (x >= GameStart.SCREEN_WIDTH - size) {
                    x = GameStart.SCREEN_WIDTH - size;  // 화면 오른쪽 경계로 위치 고정
                    velocityX *= -1;  // 방향 반전
                }

                // Y축 경계 처리
                if (y <= 0) {
                    y = 0;  // 화면 상단 경계로 위치 고정
                    velocityY *= -1;  // 방향 반전
                } else if (y >= GameStart.SCREEN_HEIGHT - size) {
                    y = GameStart.SCREEN_HEIGHT - size;  // 화면 하단 경계로 위치 고정
                    velocityY *= -1;  // 방향 반전
                }
                break;

            case "FIRE":
            case "ICE":
                // FIRE 및 ICE는 플랫폼 내에서만 이동
                if (currentPlatform != null) {
                    double platformStart = currentPlatform.getX();
                    double platformEnd = platformStart + currentPlatform.getWidth();

                    x += velocityX;

                    // 플랫폼 경계를 벗어나면 방향 반전
                    if (x <= platformStart || x + size >= platformEnd) {
                        velocityX *= -1;  // 방향 반전
                        isFacingRight = velocityX > 0; // 방향 업데이트
                    }
                }
                break;

            default:
                // 기본 이동 로직 (필요 시 추가)
                break;
            }
        }
    }
    
    public void draw(Graphics g) {
    	if (!isAlive || !isVisible || sprites == null || sprites.isEmpty()) return;
        
        BufferedImage currentSprite = sprites.get(animationIndex);
        Graphics2D g2d = (Graphics2D) g;
        int drawX = (int) x;
        int drawY = (int) y;

        if (isFacingRight) {
        	 // 오른쪽을 바라보는 경우: 좌우 반전
            AffineTransform transform = new AffineTransform();
            transform.translate(drawX + size, drawY);  // X축으로 이미지를 이동
            transform.scale(-1, 1);  // X축 반전
            transform.scale(size / (double) currentSprite.getWidth(), size / (double) currentSprite.getHeight()); // 크기 유지
            g2d.drawImage(currentSprite, transform, null);
        } else {
        	// 왼쪽을 바라보는 경우
            g2d.drawImage(currentSprite, drawX, drawY, size, size, null);
        }
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
            	defeat();          
            }
            System.out.println("Enemy hit! Remaining Hp:"+hp);
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
    public void checkCollisionWithKirby(Kirby kirby) {
        if (kirby.getBounds().intersects(this.getBounds())) {
            // 커비가 흡입 중이라면 체력 감소 X
            kirby.takeDamage(10);  // 데미지 값을 필요에 따라 조정
        }
    }
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (!visible) {
            isAlive = false;  // 보이지 않게 될 때 죽은 상태로 변경
        }
    }
} 