import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BackgroundComposer {
	
	public static BufferedImage composeBackground(String[] imagePaths, boolean isLayering) throws IOException {
        // Load all images
        BufferedImage[] images = new BufferedImage[imagePaths.length];
        for (int i = 0; i < imagePaths.length; i++) {
            images[i] = ImageIO.read(new File(imagePaths[i]));
        }

        // Calculate the total width and height of the final image
        int combinedWidth = 0;
        int combinedHeight = 0;

        if (isLayering) {
            // For layering: Use the maximum width and maximum height
            for (BufferedImage img : images) {
                combinedWidth = Math.max(combinedWidth, img.getWidth());
                combinedHeight = Math.max(combinedHeight, img.getHeight());
            }
        } else {
            // For horizontal combining: Sum of widths, max of heights
            for (BufferedImage img : images) {
                combinedWidth += img.getWidth();
                combinedHeight = Math.max(combinedHeight, img.getHeight());
            }
        }

        // Create final combined image
        BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        // Ensure transparency handling
        g.setComposite(AlphaComposite.SrcOver);

        // Draw each image
        int currentX = 0;
        for (BufferedImage img : images) {
            if (isLayering) {
                // Draw images at the same position (0, 0)
                g.drawImage(img, 0, 0, null);
            } else {
                // Draw images horizontally
                g.drawImage(img, currentX, 0, null);
                currentX += img.getWidth();
            }
        }

        // Release resources
        g.dispose();
        return combinedImage;
    }
}