package mx.uaemex.fi.linc34.efusion.mertens;

import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.min;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class LaplacianPyramid {
	
	public BufferedImage[] computePyramid(BufferedImage img) {
		
		int w = img.getWidth();
		int h = img.getHeight();
		
		return computePyramid(img, floor(log(min(w, h))/log(2)));
		
	}
	
	public BufferedImage[] computePyramid(BufferedImage img, double levels) {
		
		BufferedImage[] pyramid = new BufferedImage[(int) levels];
		
		BufferedImage aux = deepCopy(img);
		
		for (int i = 0; i < levels - 1; i++) {
			img = Sampler.downSample(aux);
			pyramid[i] = subtractImages(aux, Sampler.upSample(img));
			aux = img;
		}
		
		return pyramid;
	}
	
	private static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private static BufferedImage subtractImages(BufferedImage image1, BufferedImage image2) {
		
        int width = image1.getWidth();
        int height = image1.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = image1.getRGB(x, y);
                int rgb2 = image2.getRGB(x, y);

                // Extract red, green, and blue components
                int red1 = (rgb1 >> 16) & 0xFF;
                int green1 = (rgb1 >> 8) & 0xFF;
                int blue1 = rgb1 & 0xFF;

                int red2 = (rgb2 >> 16) & 0xFF;
                int green2 = (rgb2 >> 8) & 0xFF;
                int blue2 = rgb2 & 0xFF;

                // Perform subtraction
                int newRed = Math.abs(red1 - red2);
                int newGreen = Math.abs(green1 - green2);
                int newBlue = Math.abs(blue1 - blue2);

                // Combine components
                int newRgb = (newRed << 16) | (newGreen << 8) | newBlue;

                resultImage.setRGB(x, y, newRgb);
            }
        }

        return resultImage;
    }

}
