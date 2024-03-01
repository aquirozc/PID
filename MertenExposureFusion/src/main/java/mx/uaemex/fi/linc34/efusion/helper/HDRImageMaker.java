package mx.uaemex.fi.linc34.efusion.helper;

import java.awt.image.BufferedImage;
import java.util.stream.DoubleStream;

public class HDRImageMaker {
	
	double contrastParam = 1;
	double saturationParam = 1;
	double exposednessParam = 1;
	
	public BufferedImage bleedImages(BufferedImage[] stack){
		
		int widht = stack[0].getWidth();
		int height = stack[0].getHeight();
		int len = stack.length;
		
		BufferedImage res = new BufferedImage(widht, height, BufferedImage.TYPE_3BYTE_BGR);
		double[][][] WeightsMap = computeWeightsMap(stack);
		double[][][] relativeWeightsMap = computeRelativeWeightMap(WeightsMap);	
		
		for (int i = 0; i < widht; i++) {
			
			for (int j = 0; j < height; j++) {
				
				double rr = 0;
				double gg = 0;
				double bb = 0;
				
				for (int k = 0; k < len; k++) {
					
					int color = stack[k].getRGB(i, j);
					
					int r = (color & 0xFF0000) >> 16;
					int g = (color & 0xFF00) >> 8;
					int b = color & 0xFF;
					
					rr += r * relativeWeightsMap[i][j][k];
					gg += g * relativeWeightsMap[i][j][k];
					bb += b * relativeWeightsMap[i][j][k];
					
				}
				
				res.setRGB(i, j, ((int) rr<< 16) | ((int)gg << 8) | (int) bb);
				
			}
			
		}	
		
		
		return res;
		
	}
	
	public double[][][] computeWeightsMap(BufferedImage[] stack){
		
		int widht = stack[0].getWidth();
		int height = stack[0].getHeight();
		int len = stack.length;
		
		double sigma = 0.2d;
		double map[][][] = new double[widht][height][len];
		
		for (int k = 0; k < len; k++) {
			
			
			BufferedImage aux = stack[k];
			BufferedImage edge = Convolver.applyLinearFilter(aux,Filter.LAPLACE_KERNEL);
			
			for (int i = 0; i < widht; i++) {
				
				for (int j = 0; j < height; j++) {

					int color = aux.getRGB(i, j);
					
					int r = (color & 0xFF0000) >> 16;
					int g = (color & 0xFF00) >> 8;
					int b = color & 0xFF;
					
					//Contrast Weight
					double cWeight = Math.abs(edge.getRGB(i, j) & 0xFF) /255d;
					
					//Saturation Weight
					double mu = (r + g +b)/3d;
					//double sWeight = DoubleStream.of(r,g,b).map(x -> Math.pow(x-mu, 2)).reduce(0, (x,y) -> x + y) / 3d;
					double sWeight = (Math.pow(r-mu, 2) + Math.pow(g-mu, 2) +Math.pow(b-mu, 2))/3d;
					
					//Exposedness Weight
					double red = Math.exp(-(Math.pow(r/255d-0.5, 2)/(2*Math.pow(sigma, 2))));
					double green = Math.exp(-(Math.pow(g/255d-0.5, 2)/(2*Math.pow(sigma, 2))));
					double blue = Math.exp(-(Math.pow(b/255d-0.5, 2)/(2*Math.pow(sigma, 2))));
					
					double eWeight =  red * green * blue;
					
					map[i][j][k] = len + (Math.pow(cWeight, contrastParam)) +  Math.pow(sWeight, saturationParam) + Math.pow(eWeight, exposednessParam);
					
				}
				
			}
			
		}
		
		
		
		return map;
		
	}
	
	public double[][][] computeRelativeWeightMap(double[][][] wMap){
		
		int widht = wMap.length;
		int height = wMap[0].length;
		int len = wMap[0][0].length;
	
		double map[][][] = new double[widht][height][len];
		
		for (int i = 0; i < widht; i++) {
			
			for (int j = 0; j < height; j++) {
				
				double sum = 0;
				
				for (int k = 0; k < len; k++) {
					sum += wMap[i][j][k];
				}
				
				for (int k = 0; k < len; k++) {
					map[i][j][k] = wMap[i][j][k]/sum;
				}
				
			}
		}
		return map;
		
	}

}
