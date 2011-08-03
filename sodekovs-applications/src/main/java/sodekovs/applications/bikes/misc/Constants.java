package sodekovs.applications.bikes.misc;

public class Constants {
	

	//BucketSizeChooser determines the maximum time between two data point. -> equidistant -> start always at full hour 	
//	alle 5min -> 12 -> 300000  -> 288
//	alle 10min -> 6 -> 600000  -> 144
//	alle 15min -> 4 -> 900000  -> 96
//	alle 20min -> 3 -> 1200000 -> 72
	public static final int BUCKET_SIZE_CHOOSER_5_MIN = 5;
	public static final int BUCKET_SIZE_CHOOSER_10_MIN = 10;
	public static final int BUCKET_SIZE_CHOOSER_15_MIN = 15;
	public static final int BUCKET_SIZE_CHOOSER_20_MIN = 20;

}
