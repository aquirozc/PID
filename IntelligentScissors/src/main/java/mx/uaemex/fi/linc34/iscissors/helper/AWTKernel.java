package mx.uaemex.fi.linc34.iscissors.helper;

import java.awt.image.Kernel;

public enum AWTKernel {
	
	GAUSS_KERNEL(new float[] {
			1/16f, 1/8f, 1/16f,
		    1/8f, 1/4f, 1/8f,
		    1/16f, 1/8f, 1/16f
	}),
	
	SOBELX_KERNEL(new float[] {
			3, 0 , -3,
			10, 0 ,-10,
			3, 0, -3 
	}),
	
	SOBELY_KERNEL(new float[] {
			3, 10, 3,
			0, 0, 0, 
			-3, -10, -3			
	}),
	
	LAPLACE7X7_KERNEL (new float[]{
			-10, -5, -2, -1, -2, -5, -10,
			-5, 0, 3, 4, 3, 0, -5,
			-2, 3, 6, 7, 6, 3, -2,
			-1, 4, 7, 8, 7, 4, -1,
			-2, 3, 6, 7, 6, 3, -2,
			-5, 0, 3, 4, 3, 0, -5,
			-10, -5, -2, -1, -2, -5, -10,
	}),
	
	GAUSS5X5_KERNEL(new float[]{
		    1/273f,  4/273f,  7/273f,  4/273f,  1/273f,
		    4/273f,  16/273f,  26/273f,  16/273f,  4/273f,
		    7/273f,  26/273f,  41/273f,  26/273f,  7/273f,
		    4/273f,  16/273f,  26/273f,  16/273f,  4/273f,
		    1/273f,  4/273f,  7/273f,  4/273f,  1/273f
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
