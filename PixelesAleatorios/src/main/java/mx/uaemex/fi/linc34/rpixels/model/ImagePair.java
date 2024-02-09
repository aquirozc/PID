package mx.uaemex.fi.linc34.rpixels.model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImagePair {
	
	public Image roImage;
	public WritableImage wrImage;
	
	public ImagePair(Image roImage, WritableImage wrImage) {
		this.roImage = roImage;
		this.wrImage = wrImage;
	}

}
