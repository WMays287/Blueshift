package blueshift.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DiskImageWriter {
	
	private BufferedImage image;
	private int channel = 0; // 0 = RED, 1 = GRN, 2 = BLU
	private int xPosition = 0; // Left-to-right over image
	private int yPosition = 0; // Top-to-bottom over image
	private int plane = 0; // 0 = LSB ... 7 = MSB
	
	public DiskImageWriter(File imageSource) {
		try {
			image = ImageIO.read(imageSource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(byte[] data) {
		for (byte foo : data) {
			for (int i = 0; i < 8; i++) {
				int bit = (foo >> i) & 0x01;
				
				int rgb = image.getRGB(xPosition, yPosition);
				if (channel == 0) { // Red
					rgb = (rgb & 0xFFFEFFFF) | bit << 16;
				} else if (channel == 1) { // Grn
					rgb = (rgb & 0xFFFFFEFF) | bit << 8;
				} else if (channel == 2) { // Blu
					rgb = (rgb & 0xFFFFFFFE) | bit;
				}
				
				/* Position advance logic */
				channel++;
				if (channel < 3) continue;
				channel = 0;
				
				xPosition++;
				if (xPosition < image.getWidth()) continue;
				xPosition = 0;
				
				yPosition++;
				if (yPosition < image.getHeight()) continue;
				yPosition = 0;
				
				plane++;
				if (plane < 8) continue;
				System.err.println("Not enough space on disk image!");
				return;
				
			}
		}
	}
	
	public void exportImage(String format, File imageTarget) {
		try {
			ImageIO.write(image, format, imageTarget);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
