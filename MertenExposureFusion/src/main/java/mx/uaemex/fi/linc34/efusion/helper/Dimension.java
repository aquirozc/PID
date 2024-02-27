package mx.uaemex.fi.linc34.efusion.helper;

public class Dimension {
	
	public final int width;
	public final int height;
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Dimension substract(Dimension dim) {
		return new Dimension(this.width - dim.width, this.height - dim.height);
	}

}
