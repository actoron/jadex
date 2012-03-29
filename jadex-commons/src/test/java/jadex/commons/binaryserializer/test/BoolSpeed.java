package jadex.commons.binaryserializer.test;

import jadex.commons.transformation.binaryserializer.BinarySerializer;

import java.util.Random;

public class BoolSpeed
{
	public static void main(String[] args) throws Exception
	{
		System.in.read();
		System.out.println("Go!");
		int num = 1000000;
		Random r = new Random();
		
		boolean[] array = new boolean[num];
		for (int j = 0; j < num; ++j)
			array[j] = r.nextBoolean();
		
		for (int i = 0; i < 10; ++i)
			BinarySerializer.objectToByteArray(array, null, null, BoolSpeed.class.getClassLoader());
		
		double avg = 0;
		int runs = 200;
		for (int i = 0; i < runs; ++i)
		{
			long time = System.currentTimeMillis(); //System.nanoTime();
			BinarySerializer.objectToByteArray(array, null, null, BoolSpeed.class.getClassLoader());
			//time = System.nanoTime() - time;
			time = System.currentTimeMillis() - time;
			avg += (double) time / runs;
		}
		
		System.out.println(avg);
	}
}
