package jadex.bdi.examples.coordination.ant;

import java.security.SecureRandom;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SecureRandom rand = new SecureRandom();
		for(int i=0; i < 7; i++){
		System.out.println(rand.nextInt(7));
		}
		
		
		
		
		int a = 15; 
		
		String b = a<10 ? "ja"  : "nein";
		
		
		System.out.println(b);

		Integer d = new Integer(0);
		int res = d.intValue() + 1;
//		d = d + new Integer(1);
		System.out.println(res);
		
		
	}

}
