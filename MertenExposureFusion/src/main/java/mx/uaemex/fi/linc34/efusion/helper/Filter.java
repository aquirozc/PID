package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.image.Kernel;

public enum Filter {
	
	GAUSS5_KERNEL(new float[] {
		    1/1104f, 1/122f, 1/36f, 1/122f, 1/1104f,
		    1/122f, 1/14f, 1/5f, 1/14f, 1/122f,
		    1/36f, 1/5f, 1/2f, 1/5f, 1/36f,
		    1/122f, 1/14f, 1/5f, 1/14f, 1/122f,
		    1/1104f, 1/122f, 1/36f, 1/122f, 1/1104f
	}),
	
	GAUSS_KERNEL(new float[] {
			1/16f, 1/8f, 1/16f,
		    1/8f, 1/4f, 1/8f,
		    1/16f, 1/8f, 1/16f
	}),
	
	LAPLACE_KERNEL(new float[] {
			1f,4f,1f,
	        4f,-20f,4f,
	        1f,4f,1f
	});
	
	private Kernel instance;

	private Filter(float[] matrix) {
		
		int n = (int) Math.ceil(Math.sqrt(matrix.length));
		
		Thread.ofPlatform().start(() -> {
			instance = new Kernel(n, n, matrix);
		});
		
	}
	
	public Kernel getInstance() {
		return instance;
	}

}
