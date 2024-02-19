package mx.uaemex.fi.linc61.lfilters.helper;

public enum FXKernel {
	
	MEAN_KERNEL(new double[][]{
					{1/9d,1/9d,1/9d},
					{1/9d,1/9d,1/9d},
					{1/9d,1/9d,1/9d}
				}),
	
	GAUSS_KERNEL(new double[][]{
	    {1/16d, 1/8d, 1/16d},
	    {1/8d, 1/4d, 1/8d},
	    {1/16d, 1/8d, 1/16d}
	}),
	
	LAPLACE_KERNEL(new double[][]{
	    {0, 1/9d, 0},
	    {1/9d, -4/9d, 1/9d},
	    {0, 1/9d, 0}
	});
	
	private double[][] matrix;
	
	private FXKernel (double[][] matrix) {
		this.matrix = matrix;
	}
	
	public double getPixel(int x, int y) {
		return matrix[x][y];
	}
	
	public double[][] getMatrix() {
		return matrix;
	}

}
