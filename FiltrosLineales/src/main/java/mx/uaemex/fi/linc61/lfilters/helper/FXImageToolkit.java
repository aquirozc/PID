package mx.uaemex.fi.linc61.lfilters.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.glavo.png.javafx.PNGJavaFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXImageToolkit {
	
	private final FileChooser fChooser = new FileChooser();
	private final Window owner;
	
	public FXImageToolkit (Window owner) {
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
	
	public WritableImage toGrayScale(Image input) {
		
		int width = (int) input.getWidth();
		int height = (int) input.getHeight();
		PixelReader pReader = input.getPixelReader();
		
		WritableImage output = new WritableImage(pReader, width,height);
		
		IntStream.range(0, width).forEach(i -> {
			IntStream.range(0, height).forEach(j -> {
				
				int color = pReader.getArgb(i, j);
				
				// Demasiado lento
				//int brightness = (int) IntStream.range(0, 3).map(n -> (color & 0xFF << 8*n) >> 8*n).average().getAsDouble();
				
				int red = (color & 0xFF0000) >> 16;
				int green = (color & 0xFF00) >> 8;
				int blue = color & 0xFF;
				
				int brightness = (int)((red + green + blue)/3d);
				
				output.getPixelWriter().setArgb(i, j, (255 << 24) | (brightness << 16) | (brightness << 8) | brightness);
				
			});
		});
		
		return output;		
	}
	
	public WritableImage applyLinearFilter(Image input, FXKernel kernel) {
		
		int width = (int) input.getWidth();
		int height = (int) input.getHeight();
		
		PixelReader pReader = input.getPixelReader();
		WritableImage output = new WritableImage(pReader, width,height);	

		IntStream.range(1, width - 1).forEach(i -> {
			IntStream.range(1, height - 1).forEach(j -> {
		
				int brightness = 0;
		    	for (int x = 0; x < 3; x++) {
		    		for (int y = 0; y < 3; y++) {
		    			brightness += (pReader.getArgb(i + (x - 1), j + (y - 1)) & 0xFF) *  kernel.getPixel(x, y);
		    		}
		    	}
		         
				output.getPixelWriter().setArgb(i, j, (255 << 24) | (brightness << 16) | (brightness << 8) | brightness);
				
			});
		});
	
		return output;		
		
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
