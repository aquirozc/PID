package mx.uaemex.fi.linc34.iscissors.helper;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class LiveWire {
	
	private AWTImageToolkit toolkit = new AWTImageToolkit();
	
	private BufferedImage grayImage;
	private BufferedImage gaussImage;
	
	private double[][] laplaceZCMap;
	private double[][] gradientAbsMap;
	
	private double[][] gx;
	private double[][] gy;
	
	public double[][] pixelWeightMap;
	
	public LiveWire(BufferedImage img) {
		this.grayImage = toolkit.toGrayScale(img);
		this.gaussImage = toolkit.applyLinearFilter(grayImage, AWTKernel.GAUSS5X5_KERNEL);
		
		this.laplaceZCMap = this.getZeroCrossingMap();
		this.gradientAbsMap = this.getGradientAbsMap();
		this.pixelWeightMap = this.getPixelWeightMap();
	}
	
	public double[][] getGradientAbsMap(){
		BufferedImage dx = toolkit.applyLinearFilter(gaussImage, AWTKernel.SOBELX_KERNEL);
		BufferedImage dy = toolkit.applyLinearFilter(gaussImage, AWTKernel.SOBELY_KERNEL);
		
		double[][] map = new double[gaussImage.getWidth()][gaussImage.getHeight()];
		gx = new double[gaussImage.getWidth()][gaussImage.getHeight()];
		gy = new double[gaussImage.getWidth()][gaussImage.getHeight()];
		double max = 0;
		
		for (int i = 0; i < map.length; i++) {
		    for (int j = 0; j < map[0].length; j++) {
		        map[i][j] = Math.sqrt(Math.pow(dx.getRGB(i, j), 2) + Math.pow(dy.getRGB(i, j), 2));
		        gx[i][j] = dx.getRGB(i, j);
		        gy[i][j] = dy.getRGB(i, j);
		        max = max >= map[i][j] ? max : map[i][j];
		    }
		}
		
		for (int i = 0; i < map.length; i++) {
		    for (int j = 0; j < map[0].length; j++) {
		    	map[i][j] = 1 - map[i][j]/max; 
		    }
		}
		
		return map;
	}
	
	public double[][] getPixelWeightMap(){
		double[][] map = new double[gaussImage.getWidth()][gaussImage.getHeight()];
		IntStream.range(0, map.length).forEach(i -> {
			IntStream.range(0, map[0].length).forEach(j -> {
				map[i][j] = 0.43d * laplaceZCMap[i][j] + 0.14d * gradientAbsMap[i][j] - 0.43d * Math.atan2(gy[i][j], gx[i][j]);
			});
		});	
		return map;
	}
	
	public double[][] getZeroCrossingMap(){
		
		BufferedImage img = toolkit.applyLinearFilter(gaussImage, AWTKernel.LAPLACE7X7_KERNEL);
		
		double[][] map = new double[img.getWidth()][img.getHeight()];
		IntStream.range(0, map.length).forEach(i -> {
			IntStream.range(0, map[0].length).forEach(j -> {
				map[i][j] = img.getRGB(i, j) != 0 ? 1 : 0; 
			});
		});	
		return map;
	}
	
	public record Pixel (int x, int y) {}

}
