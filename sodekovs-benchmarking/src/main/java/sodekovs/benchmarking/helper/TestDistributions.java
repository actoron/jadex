package sodekovs.benchmarking.helper;

import java.text.DecimalFormat;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

public class TestDistributions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DecimalFormat df = new DecimalFormat( "0.00" );
//		String s = df.format( myDouble );
		
		Normal norm = new Normal(5000.0, 2500.0, RandomEngine.makeDefault());
		for(int i=0; i<50;i++){
//			System.out.println(i+ " : " + norm.nextDouble());
			double d = Math.abs(norm.nextDouble());
//			System.out.println(d + " # " + df.format( d ));
			System.out.println(df.format( d )+ " round to LONG " + Math.round(d));
		}
	
	
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
		
		
//	DecimalFormat df = new DecimalFormat( "0.00" );
//	String s = df.format( myDouble );
	
	Poisson pois = new Poisson(5000.0, RandomEngine.makeDefault());
	for(int i=0; i<50;i++){
//		System.out.println(i+ " : " + norm.nextDouble());
		double d = pois.nextDouble();
//		System.out.println(d + " # " + df.format( d ));
		System.out.println(df.format( d ));
	}
}

}
