package mx.uaemex.fi.linc61.lfilters.helper;

import java.awt.image.Kernel;

public enum AWTKernel {
	
	SHARPER_KERNEL(new float[] { 
			0, -1f, 0 ,
			-1f, 5f, -1f,
			0, -1f, 0
	}),
	
	MEAN_KERNEL(new float[] {
			1/9f, 1/9f, 1/9f,
		    1/9f, 1/9f, 1/9f,
		    1/9f, 1/9f, 1/9f
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
	
	private AWTKernel (float[] matrix) {
		
		int n = (int) Math.ceil(Math.sqrt(matrix.length));
		
		Thread.ofPlatform().start(() -> {
			instance = new Kernel(n, n, matrix);
		});
		
	}
	
	public Kernel getInstance() {
		return instance;
	}

}
