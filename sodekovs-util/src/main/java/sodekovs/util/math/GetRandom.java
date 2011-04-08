package sodekovs.util.math;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GetRandom {

	public static int getRandom(int n) {
		try { 
			// Create a secure random number generator
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			// sr.setSeed(seed);

			return sr.nextInt(n);

		} catch (NoSuchAlgorithmException e) {
		}
		return -1;
	}
}
