import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class Boss extends Enemy {
    private int phase = 1; // 현재 보스의 단계
    private int maxHp;     // 보스의 최대 체력
    private long lastAttackTime; // 마지막 공격 시간
    private long lastTeleportTime; // 마지막 순간이동 시간
    private boolean isTeleporting = false; // 순간이동 중인지 여부
    private List<BufferedImage> bossSprites; // 보스 스프라이트
    private List<BufferedImage> teleportSprites; // 순간이동 스프라이트
    private Random random = new Random();
    private List<BossProjectile> projectiles; // 발사체 리스트
    private boolean isFalling = false; // 중력 효과 적용 여부
    private double velocityY = 0;      // 수직 속도
    private double gravity = 0.5;      // 중력
    private double walkSpeed = 4;      // 걷기 속도
    private boolean movingLeft = true;
    private static final int FLOOR_Y = 600; // 바닥의 Y 좌표
    private List<Platform> platforms;
    private boolean isFlipped = false;
    private int animationIndex = 0; // 걷기 애니메이션 프레임 인덱스
    private long lastAnimationTime = 0; // 마지막 애니메이션 프레임 변경 시간
    private int animationSpeed = 200; // 애니메이션 속도 (밀리초)

    

    public Boss(int x, int y, int hp, String spriteSheetPath, List<Platform> platforms) throws IOException {
        super(x, y, "BOSS", null); // Enemy 클래스의 생성자 호출
        this.hp = hp;
        this.maxHp = hp;
        this.teleportSprites = teleportSprites;
        this.size = 80; // 보스는 더 크게 설정
        this.platforms = platforms;
        this.projectiles = new ArrayList<>(); // 발사체 리스트 초기화

        BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));

        // 걷기 스프라이트 로드
        this.bossSprites = loadBossSprites(spriteSheet, List.of(
            new SpriteInfo(0, 647, 55, 56),  // 걷기 첫 번째 프레임
            new SpriteInfo(65, 647, 55, 56),
            new SpriteInfo(125, 647, 55, 56),
            new SpriteInfo(192, 647, 55, 56)
        ));

        // 순간이동 스프라이트 로드
        this.teleportSprites = loadBossSprites(spriteSheet, List.of(
            new SpriteInfo(192, 512, 62, 62), // 순간이동 첫 번째 프레임
            new SpriteInfo(128, 512, 62, 62),
            new SpriteInfo(65, 512, 62, 62),
            new SpriteInfo(1, 512, 62, 62)
        ));
    }

    // 특정 스프라이트 잘라내기
    private List<BufferedImage> loadBossSprites(BufferedImage spriteSheet, List<SpriteInfo> spriteInfoList) {
        List<BufferedImage> sprites = new ArrayList<>();
        for (SpriteInfo info : spriteInfoList) {
            sprites.add(spriteSheet.getSubimage(info.x, info.y, info.width, info.height)); // 스프라이트 잘라내기
        }
        return sprites;
    }

    private static class SpriteInfo {
        int x, y, width, height;
        
        SpriteInfo(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    

    @Override
    public void update() {
        super.update(); // 기본 이동 로직은 유지
        long currentTime = System.currentTimeMillis();
        if (isFalling) {
            velocityY += gravity; // 중력 가속도 적용
            y += velocityY;
        }
        if (movingLeft) {
            x -= walkSpeed;
            if (x <= 0) {
                movingLeft = false;
                isFlipped = false; // 오른쪽으로 전환
            }
        } else {
            x += walkSpeed;
            if (x >= GameStart.SCREEN_WIDTH - size) {
                movingLeft = true;
                isFlipped = true; // 왼쪽으로 전환
            }
        }
        // 걷기 동작
        if (!isTeleporting && currentTime - lastAnimationTime > animationSpeed) {
            animationIndex = (animationIndex + 1) % bossSprites.size(); // 다음 프레임으로 이동
            lastAnimationTime = currentTime; // 시간 갱신
        
        	
            if (movingLeft) {
                x -= walkSpeed;
                if (x <= 0) { // 화면 왼쪽 끝에 도달하면 방향 전환
                }
            } else {
                x += walkSpeed;
                if (x >= GameStart.SCREEN_WIDTH - size) { // 화면 오른쪽 끝에 도달하면 방향 전환
                    movingLeft = true;
                }
            }
        }
        handleWalking(); // 걷기 로직 처리
        applyGravity(); // 중력 적용
        executePhaseBehavior(); // 보스의 단계별 행동 실행
        handleTeleportation(); // 순간이동 처리
        updateProjectiles(); // 발사체 업데이트
    }

    private void executePhaseBehavior() {
        long currentTime = System.currentTimeMillis();

        // 체력에 따라 행동 패턴 변경
        if (hp < maxHp * 0.5 && phase == 1) {
            phase = 2; // 50% 이하로 떨어지면 2단계로 전환
            velocityX = 4; // 이동 속도 증가
        }

        // 일정 시간마다 발사체 생성
        if (currentTime - lastAttackTime > 2000) { // 2초마다 공격
            fireProjectiles();
            lastAttackTime = currentTime;
        }
    }

    private void handleTeleportation() {
        long currentTime = System.currentTimeMillis();

        // 5초마다 순간이동 실행
        if (currentTime - lastTeleportTime > 5000) {
            isTeleporting = true; // 순간이동 상태 활성화
            teleportToRandomLocation();
            lastTeleportTime = currentTime;
        }
    }

    private void teleportToRandomLocation() {
    	if (platforms.isEmpty()) return;
        // 순간이동 위치를 랜덤으로 설정
    	Platform targetPlatform = platforms.get(random.nextInt(platforms.size()));

        x = targetPlatform.getX() + random.nextInt(targetPlatform.getWidth() - size);
        y = targetPlatform.getY() - size;

        isTeleporting = false;
    }
    private void applyGravity() {
        boolean onPlatform = false;

        // 보스가 플랫폼 위에 있는지 확인
        for (Platform platform : platforms) {
            if (getBounds().intersects(platform.getBounds())) {
                onPlatform = true;
                y = platform.getY() - size; // 플랫폼 위에 정렬
                velocityY = 0; // 중력 중지
                break;
            }
        }

        // 바닥 위에 있는지 확인
        if (y + size >= FLOOR_Y) {
            onPlatform = true;
            y = FLOOR_Y - size; // 바닥 위에 정렬
            velocityY = 0; // 중력 중지
        }

        // 플랫폼이나 바닥 위에 없으면 중력 적용
        if (!onPlatform) {
            isFalling = true;
            velocityY += gravity;
            y += velocityY;
        } else {
            isFalling = false;
        }
    }

    private void fireProjectiles() {
        // 보스의 특수 발사체 생성
        int projectileCount = phase == 1 ? 3 : 5; // 1단계: 3발, 2단계: 5발
        for (int i = 0; i < projectileCount; i++) {
            double angle = Math.toRadians(360 / projectileCount * i); // 원형으로 발사
            double velocityX = Math.cos(angle) * 5;
            double velocityY = Math.sin(angle) * 5;
            projectiles.add(new BossProjectile((int) x + size / 2, (int) y + size / 2, velocityX, velocityY));
        }
    }

    private void updateProjectiles() {
        Iterator<BossProjectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            BossProjectile projectile = iterator.next();
            projectile.update();

            // 화면 밖으로 나간 발사체 제거
            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }
    private void handleWalking() {
        if (isTeleporting) return; // 순간이동 중일 때는 걷지 않음

        if (movingLeft) {
            x -= walkSpeed;
            if (x <= 0) { // 화면 왼쪽 끝에 도달하면 방향 전환
                movingLeft = false;
                isFlipped=false;
            }
        } else {
            x += walkSpeed;
            if (x >= GameStart.SCREEN_WIDTH - size) { // 화면 오른쪽 끝에 도달하면 방향 전환
                movingLeft = true;
                isFlipped=true;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isAlive) return;
        BufferedImage currentSprite;
        
        // 순간이동 상태에 따른 스프라이트 변경
        if (isTeleporting) {
            int teleportFrame = (int) ((System.currentTimeMillis() / 100) % teleportSprites.size());
            currentSprite = teleportSprites.get(teleportFrame); // 순간이동 애니메이션
        } else {
            currentSprite = bossSprites.get(animationIndex); // 단계에 따라 스프라이트 변경
        }
        if (isFlipped) {
            drawFlippedImage(g, currentSprite, (int) x, (int) y);
        } else {
            g.drawImage(currentSprite, (int) x, (int) y, size, size, null);
        }

        // 체력 게이지 표시
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 20, (int) (size * (hp / (double) maxHp)), 10);

        // 발사체 그리기
        for (BossProjectile projectile : projectiles) {
            projectile.draw(g);
        }
    }
    private void drawFlippedImage(Graphics g, BufferedImage image, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;

        // 수평으로 이미지 뒤집기
        g2d.drawImage(
            image,
            x + size, y, // 뒤집어진 이미지를 그릴 위치
            x, y + size, // 뒤집어진 이미지를 끝낼 위치
            0, 0, // 원본 이미지의 시작 위치
            image.getWidth(), image.getHeight(), // 원본 이미지의 끝 위치
            null
        );
    }

    // 발사체 내부 클래스
    private static class BossProjectile {
        private int x, y;
        private double velocityX, velocityY;
        private boolean active = true;

        public BossProjectile(int x, int y, double velocityX, double velocityY) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }

        public void update() {
            x += velocityX;
            y += velocityY;

            // 화면 밖으로 나가면 비활성화
            if (x < 0 || x > GameStart.SCREEN_WIDTH || y < 0 || y > GameStart.SCREEN_HEIGHT) {
                active = false;
            }
        }

        public void draw(Graphics g) {
            g.setColor(Color.YELLOW); // 발사체 색상
            g.fillOval(x, y, 10, 10); // 발사체 크기
        }

        public boolean isActive() {
            return active;
        }
    }
}
