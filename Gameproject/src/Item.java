import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Item {
    private int x, y;
    private int width, height;
    private String type;  // "HEALTH", "ENERGY", "POINTS"
    private boolean isActive = true;
    private BufferedImage sprite;

    public Item(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.width = 25;
        this.height = 25;
        loadSprite();
    }
    
    private void loadSprite() {
        try {
            // 타입에 따라 다른 이미지 로드
            if (type.equals("HEALTH")) {
                sprite = ImageIO.read(new File("img/potion_01_red.png"));  // 빨간 물약 경로
            } else if (type.equals("ENERGY")) {
                sprite = ImageIO.read(new File("img/potion_02_blue.png"));  // 파란 물약 경로
            }
        } catch (IOException e) {
            System.out.println("Failed to load sprite for type " + type + ": " + e.getMessage());
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }


    public void collect(Kirby player) {
        if (!isActive) return;

        switch (type) {
            case "HEALTH":
                player.heal(50);
                break;
            case "ENERGY":
                player.rechargeEnergy(30);
                break;
      
        }
        isActive = false;
    }

    public void draw(Graphics g) {
        if (!isActive) return;

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);  // 스프라이트 그리기
        } else {
            // 스프라이트가 없는 경우 기본 도형으로 그리기
            switch (type) {
                case "HEALTH":
                    g.setColor(Color.RED);
                    break;
                case "ENERGY":
                    g.setColor(Color.BLUE);
                    break;
            }
            g.fillRect(x, y, width, height);
        }
    }
}

