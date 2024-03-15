package mx.uaemex.fi.linc34.iscissors.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.glavo.png.javafx.PNGJavaFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXImageIO {
	
	private final Random random = ThreadLocalRandom.current();
	private final FileChooser fChooser = new FileChooser();
	private final Window owner;
	
	public FXImageIO (Window owner) {
		this.owner = owner;
		
		fChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.tiff", "*.ico", "*.webp"));
	}
	
	public Image loadImageFromArgs(List<String> clArgs) {
		
		try{
			return new Image(new FileInputStream(clArgs.getFirst()));
		} catch (IOException | NoSuchElementException | NullPointerException e) {
			return loadImageManually();
		}
		
	}
	
	public Image loadImageManually() {
		
		Image img = null;
		
		File file = fChooser.showOpenDialog(owner);
		
		try(FileInputStream fStream = new  FileInputStream(file)) {
			return new Image(fStream);
		} catch (Exception e) {}	
		
		return img;
	}
	
	public void overrideRegionRandom(WritableImage img, int x0, int x1, int y1, int y2) throws IndexOutOfBoundsException {

		if (img == null) return;
		
		IntStream.rangeClosed(x0, x1).forEach(i -> {
			IntStream.rangeClosed(y1, y2).forEach(j -> {
				
				int rgba =  (255 << 24) | ( random.nextInt(256) << 16) | (random.nextInt(256) << 8) | random.nextInt(256);
			
				img.getPixelWriter().setArgb(i, j, rgba);
				
			});
		});
		
		
	}
	
	public void saveImageToDisk(WritableImage img) {
		
		File out = fChooser.showSaveDialog(owner);
		
		try {
			PNGJavaFXUtils.writeImage(img, out.toPath());
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		   	
	}

}
