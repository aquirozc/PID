package mx.uaemex.fi.linc34.iscissors.helper;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;

public class AWTImageToolkit {
	
	public BufferedImage toGrayScale(BufferedImage img) {
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage grayImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        op.filter(img, grayImage);
        return grayImage;	
	}
	
	public BufferedImage applyLinearFilter(BufferedImage img, AWTKernel kernel) {
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	    new ConvolveOp(kernel.getInstance()).filter(img, out);
	    return out;
	}
	
}
