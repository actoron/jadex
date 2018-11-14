package jadex.commons.random;

import java.lang.reflect.Constructor;
import java.util.Random;

import jadex.commons.SUtil;

/**
 *  Wrapper for quick multi-threaded PRNG access using multiple PRNGs.
 *
 */
public class FastThreadedRandom extends Random
{
	/** ID */
	private static final long serialVersionUID = -1193190439562997946L;
	
	/** The random number generators. */
	protected Random[] prngs;
	
	/** Mask for distributing threads. */
	protected int threadingmask;
	
	/**
	 *  Creates the wrapper.
	 */
	public FastThreadedRandom()
	{
		this(Xoroshiro128Random.class);
	}
	
	/**
	 *  Creates the wrapper.
	 */
	public FastThreadedRandom(Class<?> randomclazz)
	{
		this(randomclazz, 4);
	}
	
	/**
	 *  Creates the wrapper.
	 */
	@SuppressWarnings("unchecked")
	public FastThreadedRandom(Class<?> randomclazz, int threadinglevel)
	{
		threadinglevel = Math.min(Math.abs(threadinglevel), 31);
		prngs = new Random[1 << threadinglevel];
		this.threadingmask = prngs.length - 1;
		try
		{
			Constructor<Random> con = (Constructor<Random>) randomclazz.getConstructor(new Class[0]);
			for (int i = 0; i < prngs.length; ++i)
			{
				prngs[i] = con.newInstance((Object[]) null);
			}
		}
		catch (Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public boolean nextBoolean()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextBoolean();
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public void nextBytes(byte[] bytes)
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			r.nextBytes(bytes);
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public double nextDouble()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextDouble();
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public float nextFloat()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextFloat();
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public double nextGaussian()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextGaussian();
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public int nextInt()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextInt();
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public int nextInt(int bound)
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextInt(bound);
		}
	}
	
	/**
	 *  Wrapper method.
	 */
	public long nextLong()
	{
		Random r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
		synchronized (r)
		{
			return r.nextLong();
		}
	}
	
	/**
	 *  Wrapper method.
	 *  Warning: This is useless, included for completeness.
	 */
	public void setSeed(long seed)
	{
	}
}
