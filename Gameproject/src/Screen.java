import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class Screen extends Canvas {
	private Graphics bufferGraphics;
	private Image offScreen;
	private Dimension dim;
	
	private void initBuffer() {
		this.dim = getSize();
		this.offScreen = createImage(dim.width, dim.height);
		this.bufferGraphics = this.offScreen.getGraphics();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

}
