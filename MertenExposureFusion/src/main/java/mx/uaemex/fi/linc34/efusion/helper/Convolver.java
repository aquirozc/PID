package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;

public class Convolver {
	
	public static BufferedImage toGrayScale(BufferedImage img) {
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage grayImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        op.filter(img, grayImage);
        return grayImage;	
	}
	
	public static BufferedImage applyLinearFilter(BufferedImage img, Filter kernel) {
		
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

	    new ConvolveOp(kernel.getInstance()).filter(img, out);

	    return out;
		
	}

}
