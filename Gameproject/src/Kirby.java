import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Kirby {
    private double x, y;
    private double velocityX = 0;
    private double velocityY = 0;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_FORCE = -12;
    private static final double MOVE_SPEED = 5;
    private boolean isInhaling = false;
    private int size = 50;
    private String currentAbility = "NORMAL";
    private int hp = 100;
    private boolean facingRight = true;
    private boolean isOnGround = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private BufferedImage spriteSheet;
    private BufferedImage[] walkSprites;
    private BufferedImage idleSprite;
    private int currentFrame = 0;
    private int frameDelay = 0;
    private static final int FRAME_DELAY_RATE = 5;  // 프레임 변경 속도
    private BufferedImage[] inhaleSprites;  // 클래스 필드에 추가
    
    
    public Kirby(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        loadSprites();
    }
    
    private void loadSprites() {
        try {
            spriteSheet = ImageIO.read(new File("img/kirby.png"));
            walkSprites = new BufferedImage[10];
            inhaleSprites = new BufferedImage[2];  // 흡입 스프라이트 배열 추가
            
            // 스프라이트 위치와 크기 지정
            int spriteWidth = 22;   // 실제 스프라이트의 너비
            int spriteHeight = 20;  // 실제 스프라이트의 높이
            
            // 걷기 애니메이션 스프라이트 위치
            int walkStartX = 253;     // 걷기 스프라이트 시작 X 좌표
            int walkStartY = 8;     // 걷기 스프라이트 시작 Y 좌표
            
            // 대기 스프라이트 위치
            int idleX = 8;    // 대기 스프라이트 X 좌표
            int idleY = 8;     // 대기 스프라이트 Y 좌표
            
            // 흡입 스프라이트 위치
            int inhaleStartX = 725;  // 실제 스프라이트 시트의 흡입 모션 X 좌표로 수정 필요
            int inhaleStartY = 180;   // 실제 스프라이트 시트의 흡입 모션 Y 좌표로 수정 필요
            
            // 걷기 스프라이트 추출
            for (int i = 0; i < 10; i++) {
                walkSprites[i] = spriteSheet.getSubimage(
                    walkStartX + (i * spriteWidth),
                    walkStartY,
                    spriteWidth,
                    spriteHeight
                );
            }
            
            // 대기 스프라이트 추출 (단일 이미지)
            idleSprite = spriteSheet.getSubimage(idleX, idleY, spriteWidth, spriteHeight);
            
         // 흡입 스프라이트 추출
            for (int i = 0; i < 2; i++) {
                inhaleSprites[i] = spriteSheet.getSubimage(
                    inhaleStartX + (i * spriteWidth),
                    inhaleStartY,
                    spriteWidth,
                    spriteHeight
                );
            }
            
        } catch (IOException e) {
            System.out.println("스프라이트를 로드할 수 없습니다: " + e.getMessage());
        }
    }
    
    public void update() {
        // 중력 적용
        velocityY += GRAVITY;
        
        // 이동 처리
        if (isMovingLeft) {
            velocityX = -MOVE_SPEED;
            facingRight = false;
        }
        if (isMovingRight) {
            velocityX = MOVE_SPEED;
            facingRight = true;
        }
        if (!isMovingLeft && !isMovingRight) {
            velocityX *= 0.9;  // 감속
        }
        
        // 위치 업데이트
        x += velocityX;
        y += velocityY;
        
        isOnGround = false;  // 매 프레임마다 초기화
        
        // 바닥 충돌 체크
        if (y > 550) {  // 600(바닥) - size(50)
            y = 550;
            velocityY = 0;
            isOnGround = true;
        }
        
        // 화면 경계 체크
        if (x < 0) x = 0;
        if (x > GameStart.SCREEN_WIDTH - size) x = GameStart.SCREEN_WIDTH - size;
        
        // 플랫폼 충돌 체크
        for (Platform platform : GameStart.platforms) {
            if (getBounds().intersects(platform.getBounds())) {
                if (velocityY > 0) {  // 떨어지는 중
                    y = platform.getBounds().y - size;
                    velocityY = 0;
                    isOnGround = true;
                }
            }
        }
        
        // 애니메이션 프레임 업데이트
        if (isMovingLeft || isMovingRight) {  // 움직일 때만 프레임 업데이트
            frameDelay++;
            if (frameDelay >= FRAME_DELAY_RATE) {
                frameDelay = 0;
                currentFrame = (currentFrame + 1) % 4;
            }
        } else {
            currentFrame = 0;  // 멈춰있을 때는 첫 프레임으로
        }
    }
    
    public void moveLeft() {
        velocityX = -MOVE_SPEED;
        facingRight = false;
    }
    
    public void moveRight() {
        velocityX = MOVE_SPEED;
        facingRight = true;
    }
    
    public void jump() {
        if (isOnGround) {  // 바닥이나 플랫폼 위에 있을 때만 점프 가능
            velocityY = JUMP_FORCE;
            isOnGround = false;
        }
    }
    
    public void startInhale() {
        isInhaling = true;
    }
    
    public void stopInhale() {
        isInhaling = false;
    }
    
    public void copyAbility(Enemy enemy) {
        if (isInhaling && enemy.isAlive()) {
            // 방향에 따른 흡입 가능 범위 확인
            Rectangle inhaleArea;
            if (facingRight) {
                inhaleArea = new Rectangle((int)x + size, (int)y, 30, 50);
            } else {
                inhaleArea = new Rectangle((int)x - 30, (int)y, 30, 50);
            }
            
            // 흡입 범위 안에 적이 있을 때만 능력 복사
            if (inhaleArea.intersects(enemy.getBounds())) {
                currentAbility = enemy.getType();
                enemy.defeat();
            }
        }
    }
    
    public void useAbility() {
        switch(currentAbility) {
            case "FIRE":
                // 불 뿜기 효과
                break;
            case "ICE":
                // 얼음 공격 효과
                break;
        }
    }
  

    
    public void draw(Graphics g) {
        if (spriteSheet == null) {
            drawBasicKirby(g);
            return;
        }
        
        // 스프라이트 그리기
        BufferedImage currentSprite;
        if (isInhaling) {
            currentSprite = inhaleSprites[currentFrame % 2];  // 흡입 애니메이션
        } else if (isMovingLeft || isMovingRight) {
            currentSprite = walkSprites[currentFrame];
        } else {
            currentSprite = idleSprite;
        }
        
        // 이미지 뒤집기 (왼쪽 볼 때)
        if (!facingRight) {
            g.drawImage(currentSprite, (int)x + size, (int)y, -size, size, null);
        } else {
            g.drawImage(currentSprite, (int)x, (int)y, size, size, null);
        }
        
        // 흡입 효과
        if (isInhaling) {
        	drawInhaleEffect(g);
        }
        
        // 상태 표시 (HP바, 능력 등)
        drawStatus(g);
    }
    
    private void drawBasicKirby(Graphics g) {
        // 기존의 원형 커비 그리기 코드
        g.setColor(Color.PINK);
        g.fillOval((int)x, (int)y, size, size);
        
        // 눈과 볼 위치 계산
        int eyeOffsetX = facingRight ? 10 : 30;
        int cheekOffsetX = facingRight ? 5 : 35;
        
        // 눈 그리기
        g.setColor(Color.BLACK);
        g.fillOval((int)x + eyeOffsetX, (int)y + 15, 8, 8);
        g.fillOval((int)x + (facingRight ? 30 : 10), (int)y + 15, 8, 8);
        
        // 볼 그리기
        g.setColor(new Color(255, 182, 193));
        g.fillOval((int)x + cheekOffsetX, (int)y + 25, 10, 10);
    }
    
    private void drawStatus(Graphics g) {
        // 능력 상태 표시
        g.setColor(Color.WHITE);
        g.drawString("Ability: " + currentAbility, (int)x, (int)y - 10);
        
        // HP 바
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 20, (int)(size * (hp/100.0)), 5);
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
    
    public boolean isInhaling() {
        return isInhaling;
    }
    public boolean isAlive() {
    	return hp >0;
    }
    public void setMovingLeft(boolean moving) {
        isMovingLeft = moving;
    }
    
    public void setMovingRight(boolean moving) {
        isMovingRight = moving;
    }
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;  // 체력이 0 이하로 내려가지 않도록 처리
            System.out.println("Kirby has been defeated!");
            // 게임 오버 처리 로직을 여기에 추가 가능
        }
    }
    public int getHp() {
        return hp;
    }

    
    private void drawInhaleEffect(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        
        if (facingRight) {
            for (int i = 1; i <= 5; i++) {
                int width = 30 - i * 4;
                int height = 40 - i * 4;
                g2d.setColor(new Color(255, 255, 255, 200 - i * 30));
                g2d.fillOval((int)x + size + i * 10, (int)y + 5, width, height);
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                int width = 30 - i * 4;
                int height = 40 - i * 4;
                g2d.setColor(new Color(255, 255, 255, 200 - i * 30));
                g2d.fillOval((int)x - 30 - i * 10, (int)y + 5, width, height);
            }
        }
        g2d.dispose();
    }
} 
 