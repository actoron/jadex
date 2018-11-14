package jadex.commons.random;

import java.util.Random;

/**
 *  xoroshiro128+ implementation based on work by David Blackman and Sebastiano Vigna.
 *  http://xoroshiro.di.unimi.it/xoroshiro128plus.c
 *	
 *	To the extent possible under law, the author has dedicated all copyright
 *	and related and neighboring rights to this software to the public domain
 *	worldwide. This software is distributed without any warranty.
 *
 *	See <http://creativecommons.org/publicdomain/zero/1.0/>.
 *
 */
public class Xoroshiro128Random extends Random
{
	protected static final double DOUBLE_BASE = 0x1.0p-53;
	long state0;
	long state1;
	
	public Xoroshiro128Random()
	{
//		state = new long[2];
		Random sr = new Random();
		state0 = sr.nextLong();
		state1 = sr.nextLong();
	}
	
	public Xoroshiro128Random(long[] state)
	{
		assert state != null && state.length == 2;
		this.state0 = state[0];
		this.state1 = state[1];
	}
	
	public int next(int bits)
	{
		return (int)(nextLong() >>> (64 - bits));
	}
	
	@Override
	public double nextDouble()
	{
		return (nextLong() >>> 11) * DOUBLE_BASE;
	}
	
	public long nextLong()
	{
		long s0 = state0;
		long s1 = state1;
		long result = s0 + s1;
	
		s1 ^= s0;
		state0 = Long.rotateLeft(s0, 55) ^ s1 ^ (s1 << 14);
		state1 = Long.rotateLeft(s1, 36);
	
		return result;
	}
}
