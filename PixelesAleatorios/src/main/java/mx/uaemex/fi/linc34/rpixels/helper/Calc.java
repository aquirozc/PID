package mx.uaemex.fi.linc34.rpixels.helper;

import java.util.stream.DoubleStream;

public class Calc {
	
	public static int min(double... args) {
		return (int) DoubleStream.of(args).min().getAsDouble();
	}

}
