import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Item {
    private int x, y;
    private static final int SIZE = 25; // Unified size for all items
    private String type;  // Item type: "HEALTH", "ENERGY", etc.
    private boolean isActive = true; // State flag
    private BufferedImage sprite; // Sprite for visual representation

    public Item(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        loadSprite();
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

