import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Item {
    private int x, y;
    private static final int SIZE = 25; // Unified size for all items
    private String type;  // Item type: "HEALTH", "ENERGY", etc.
    private boolean isActive = true; // State flag
    private BufferedImage sprite; // Sprite for visual representation
    private int velocityY = 0;
    private final int gravity = 1;
    private static final int groundLevel = 600;
    private int rotationAngle = 0;
    private static final Random rand = new Random();
    private long spawnTime;
    private static final int LIFESPAN = 10000; // 10초

    public Item(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.type = randomItemType();
        this.spawnTime = System.currentTimeMillis();
        loadSprite();
    }
    private String randomItemType() {
        String[] itemTypes = {"HEALTH", "ENERGY"};
        return itemTypes[rand.nextInt(itemTypes.length)]; // 배열에서 랜덤으로 아이템 선택
    }

    private void loadSprite() {
        try {
            if (type.equals("HEALTH")) {
                sprite = ImageIO.read(new File("img/potion_01_red.png")); // Red potion
            } else if (type.equals("ENERGY")) {
                sprite = ImageIO.read(new File("img/potion_02_blue.png")); // Blue potion
            }
        } catch (IOException e) {
            System.out.println("Failed to load sprite for type " + type + ": " + e.getMessage());
        }
    }
    public void update() {
        if (!isActive) return;
        rotationAngle += 5; // 매 프레임마다 5도 회전
        if (rotationAngle >= 360) {
            rotationAngle = 0; // 360도를 넘어가면 0으로 초기화
        }

        // 중력 적용
        velocityY += gravity; // 수직 속도에 중력 추가
        y+=velocityY;

        // 아이템이 바닥에 닿았는지 확인
        if (y + SIZE + velocityY >= groundLevel) {
            velocityY = 0; // 바닥에 닿으면 낙하 멈춤
            y = groundLevel - SIZE; // 아이템을 바닥 높이에 맞게 조정
        } else {
            y += velocityY; // 낙하하는 아이템의 위치를 업데이트
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public void collect(Kirby player) {
        if (!isActive) return;

        switch (type) {
            case "HEALTH":
                player.heal(30); // Heal effect
                break;
            case "ENERGY":
                player.rechargeEnergy(30); // Energy effect
                break;
        }

        isActive = false; // Item is consumed
    }
    
    public static Item spawnRandomItem() {
    	int x = rand.nextInt(GameStart.SCREEN_WIDTH - SIZE); // 화면 가로 범위 내
        int y = rand.nextInt(groundLevel - SIZE); // 바닥 위 랜덤 생성
        String[] types = {"HEALTH", "ENERGY"};
        String type = types[rand.nextInt(types.length)];
        return new Item(x, y, type); // 랜덤 아이템 생성
    }
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > LIFESPAN;
    }

    public void draw(Graphics g) {
        if (!isActive) return;

        if (sprite != null) {
            g.drawImage(sprite, x, y, SIZE, SIZE, null); // Draw sprite
        } else {
            // Fallback to simple shapes
            switch (type) {
                case "HEALTH":
                    g.setColor(Color.RED);
                    break;
                case "ENERGY":
                    g.setColor(Color.BLUE);
                    break;
            }
            g.fillRect(x, y, SIZE, SIZE);
        }
    }
}

