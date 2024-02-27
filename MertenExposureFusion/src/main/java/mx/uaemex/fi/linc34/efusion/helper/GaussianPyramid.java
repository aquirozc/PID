package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class GaussianPyramid {
	
	public BufferedImage[] computePyramid(BufferedImage img) {
		
		int w = img.getWidth();
		int h = img.getHeight();
		
		return computePyramid(img, floor(log(min(w, h))/log(2)));
		
	}
	
	public BufferedImage[] computePyramid(BufferedImage img, double levels) {
		
		BufferedImage[] pyramid = new BufferedImage[(int) levels];
		pyramid[0] = img;
		
		IntStream.range(1, (int) levels).forEach(i ->{
			pyramid[i] = Sampler.downSample(pyramid[i-1]);
		});
		
		return pyramid;
	}

}
