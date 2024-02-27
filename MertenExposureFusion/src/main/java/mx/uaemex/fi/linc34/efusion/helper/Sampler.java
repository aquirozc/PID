package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class Sampler {
	
	public static BufferedImage downSample(BufferedImage img){
		
		int w = img.getWidth()/2;
		int h = img.getHeight()/2;
		
		BufferedImage smooth = Convolver.applyLinearFilter(img, Filter.GAUSS_KERNEL);	
		BufferedImage sample = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		IntStream.range(0, w).forEach(i -> {
			IntStream.range(0, h).forEach(j -> {
				sample.setRGB(i, j, smooth.getRGB(2*i, 2*j));
			});
		});
		
		return img;
	}
	
	public static BufferedImage upSample(BufferedImage img) {
		
		int w = img.getWidth()*2;
		int h = img.getHeight()*2;
		
        BufferedImage resizedImg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImg.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(img, 0, 0, w,h, null);
        g2d.dispose();
		
		return Convolver.applyLinearFilter(resizedImg, Filter.GAUSS_KERNEL);
		
	}

}
