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
    private int inhaleFrame = 0;
    private static final int INHALE_FRAME_RATE = 4;  // 흡입 애니메이션 속도 조절
    private boolean isStartingInhale = false;  // 흡입 시작 여부
    private int inhaleDelay = 0;
    
    public Kirby(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        loadSprites();
    }
    
    private class SpriteInfo {
        int x, y, width, height;
        
        SpriteInfo(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    private void loadSprites() {
        try {
            spriteSheet = ImageIO.read(new File("img/kirby.png"));
            walkSprites = new BufferedImage[9];
            inhaleSprites = new BufferedImage[6];  // 흡입 스프라이트 배열 추가
            
         // 각 스프라이트의 위치와 크기 정보 설정
            SpriteInfo[] walkInfo = {
                new SpriteInfo(254, 9, 20, 19),  // 걷기 1
                new SpriteInfo(274, 9, 23, 19),  // 걷기 2
                new SpriteInfo(319, 11, 21, 17),  // 걷기 3
                new SpriteInfo(340, 10, 19, 18),  // 걷기 4
                new SpriteInfo(359, 10, 19, 18),  // 걷기 5
                new SpriteInfo(379, 8, 23, 20),  // 걷기 6
                new SpriteInfo(402, 8, 22, 20),  // 걷기 7
                new SpriteInfo(424, 11, 21, 17),  // 걷기 8
                new SpriteInfo(446, 10, 20, 18),  // 걷기 9
            };
            
            SpriteInfo[] inhaleInfo = {
                new SpriteInfo(772, 179, 21, 21),    // 흡입 1 (더 큰 크기)
                new SpriteInfo(748, 177, 22, 23),    // 흡입 2 (다른 크기)
                new SpriteInfo(794, 178, 25, 22),    // 흡입 3
                new SpriteInfo(820, 177, 23, 23),    // 흡입 4
                new SpriteInfo(844, 179, 24, 22),    // 흡입 5
                new SpriteInfo(868, 177, 24, 23),    // 흡입 6
            };
            
            // 대기 스프라이트 정보
            SpriteInfo idleInfo = new SpriteInfo(8, 8, 20, 20);
            
         // 걷기 스프라이트 추출
            for (int i = 0; i < walkSprites.length; i++) {
                SpriteInfo info = walkInfo[i];
                walkSprites[i] = spriteSheet.getSubimage(
                    info.x, info.y, info.width, info.height
                );
            }
            
            // 대기 스프라이트 추출
            idleSprite = spriteSheet.getSubimage(
                idleInfo.x, idleInfo.y, idleInfo.width, idleInfo.height
            );
            
            // 흡입 스프라이트 추출
            for (int i = 0; i < inhaleSprites.length; i++) {
                SpriteInfo info = inhaleInfo[i];
                inhaleSprites[i] = spriteSheet.getSubimage(
                    info.x, info.y, info.width, info.height
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
        if (isInhaling) {
            inhaleDelay++;
            if (inhaleDelay >= INHALE_FRAME_RATE) {
                inhaleDelay = 0;
                if (isStartingInhale) {
                    // 시작 애니메이션 
                    inhaleFrame++;
                    if (inhaleFrame >= 3) {  // 처음 3프레임이 끝나면
                        isStartingInhale = false;
                        inhaleFrame = 4;  // 4번 프레임부터 반복 시작
                    }
                } else {
                    // 4,5번 프레임만 반복
                    inhaleFrame = (inhaleFrame == 4) ? 5 : 4;
                }
            }
        } else {
            inhaleFrame = 0;
        }
        
        // 걷기 애니메이션 업데이트
        if (isMovingLeft || isMovingRight) {
            frameDelay++;
            if (frameDelay >= FRAME_DELAY_RATE) {
                frameDelay = 0;
                currentFrame = (currentFrame + 1) % 9;
            }
        } else {
            currentFrame = 0;
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
            currentSprite = inhaleSprites[inhaleFrame];  // 별도의 흡입 프레임 사용
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
    
    public void setMovingLeft(boolean moving) {
        isMovingLeft = moving;
    }
    
    public void setMovingRight(boolean moving) {
        isMovingRight = moving;
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
 