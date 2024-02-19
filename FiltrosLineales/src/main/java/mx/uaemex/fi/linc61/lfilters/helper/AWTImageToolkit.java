package mx.uaemex.fi.linc61.lfilters.helper;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

public class AWTImageToolkit {
	
	public AWTImageToolkit() {
		super();
	}
	
	public BufferedImage loadImageFromArgs(List<String> clArgs) {
		
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File(clArgs.getFirst()));
		} catch (Exception e) {
			return loadImageManually();
		}
		
		return img;
		
	}
	
	public BufferedImage loadImageManually() {
		return null;
	}
	
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
