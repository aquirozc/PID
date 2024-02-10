package mx.uaemex.fi.linc34.rpixels.helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXImageIO {
	
	private final Random random = ThreadLocalRandom.current();
	private final FileChooser fChooser = new FileChooser();
	private final Window owner;
	
	public FXImageIO (Window owner) {
		this.owner = owner;
	}
	
	public Image loadImageFromArgs(List<String> clArgs) {
		
		if (clArgs == null || clArgs.isEmpty()) {
			return loadImageManually();
		}
		
		return new Image(clArgs.getFirst());
		
	}
	
	public Image loadImageManually() {
		
		Image img = null;
		
		while (img == null) {
			
			File file = fChooser.showOpenDialog(owner);
			
			try {
				img = new Image(new FileInputStream(file));
			} catch (FileNotFoundException | NullPointerException e) {
				System.out.println("Tiene que seleccionar una imagen");
			}
			
		}
		
		return img;
	}
	
	public void overrideRegionRandom(WritableImage img, int x0, int x1, int y1, int y2) {
		
		IntStream.rangeClosed(x0, x1).forEach(i -> {
			IntStream.rangeClosed(y1, y2).forEach(j -> {
				
				double r = random.nextInt(256) / 255d;
				double g = random.nextInt(256) / 255d;
				double b = random.nextInt(256) / 255d;
				
				img.getPixelWriter().setColor(i, j, new Color(r, g, b, 1));
				
			});
		});
		
		
	}
	
	public void saveImageToDisk(WritableImage img) {
		
		File out = fChooser.showSaveDialog(owner);
		
				
	}

}
