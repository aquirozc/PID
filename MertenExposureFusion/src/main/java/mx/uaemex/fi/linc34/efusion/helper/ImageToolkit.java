package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.glavo.png.javafx.PNGJavaFXUtils;

import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ImageToolkit {
	
	private final FileChooser fChooser = new FileChooser();
	private final DirectoryChooser dChooser = new DirectoryChooser();
	private final HDRImageMaker hdrMaker = new HDRImageMaker();
	private final Window owner;
	
	public ImageToolkit (Window owner) {
		this.owner = owner;
		fChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.tiff", "*.ico", "*.webp"));
	}
	
	public File changeWorkingDirectory() {
		return dChooser.showDialog(owner);
	}
	
	public BufferedImage generateHDRImage(File dir) {
		
		BufferedImage[] stack = Stream.of(dir.listFiles())
									.map(this::toBufferedImage)
									.filter(i -> i != null)
									.toArray(BufferedImage[]::new);
		
		return hdrMaker.bleedImages(stack);
		
	}
	
	private BufferedImage toBufferedImage(File file) {
		
		try {
			return ImageIO.read(file);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public void saveImageToDisk(WritableImage img) {
		
		File out = fChooser.showSaveDialog(owner);
		
		if(img == null || out == null) { return;}
		
		try {
			PNGJavaFXUtils.writeImage(img, out.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		   	
	}
	
	

}
