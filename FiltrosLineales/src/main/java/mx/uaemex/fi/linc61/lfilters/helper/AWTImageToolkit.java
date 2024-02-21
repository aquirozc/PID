package mx.uaemex.fi.linc61.lfilters.helper;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.nio.IntBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

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
		//TYPE_INT_RGB lento pero necesario para aplicar los filtros
        BufferedImage grayImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        op.filter(img, grayImage);
        return grayImage;	
	}
	
	public BufferedImage applyLinearFilter(BufferedImage img, AWTKernel kernel) {
		
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

	    new ConvolveOp(kernel.getInstance()).filter(img, out);

	    return out;
		
	}
	
	//Más rapido que usar la SwingFXUtils
	//https://stackoverflow.com/questions/30970005/bufferedimage-to-javafx-image
	
	public Image converToSwingImage(BufferedImage img) {
		 //converting to a good type, read about types here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
	    BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
	    newImg.createGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

	    //converting the BufferedImage to an IntBuffer
	    int[] type_int_agrb = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
	    IntBuffer buffer = IntBuffer.wrap(type_int_agrb);

	    //converting the IntBuffer to an Image, read more about it here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
	    PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	    PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(newImg.getWidth(), newImg.getHeight(), buffer, pixelFormat);
	    return new WritableImage(pixelBuffer);
	}

}
