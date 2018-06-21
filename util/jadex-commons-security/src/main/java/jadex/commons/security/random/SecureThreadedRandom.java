package jadex.commons.security.random;

import java.security.SecureRandom;

/**
 *  Wrapper for quick multi-threaded PRNG access using multiple PRNGs.
 *
 */
public class SecureThreadedRandom extends SecureRandom
{
	/** If true, minimize the initial entropy consumption. */
	private static final boolean MINIMIZE_INITIAL_ENTROPY_CONSUMPTION = true;
	
	/** ID */
	private static final long serialVersionUID = -11931962997946L;
	
	/** The random number generators. */
//	protected AtomicReference<SecureRandom>[] prngs;
	protected SecureRandom[] prngs;
	
	/** Mask for distributing threads. */
	protected int threadingmask;
	
	/**
	 *  Creates the wrapper.
	 */
	public SecureThreadedRandom()
	{
		int tl = Runtime.getRuntime().availableProcessors();
		tl <<= 2;
		tl = Integer.numberOfTrailingZeros(Integer.highestOneBit(tl));
		tl = Integer.bitCount(tl) != 1 ? tl << 1 : tl;
		tl = Math.min(Math.abs(tl), 31);
		
		prngs = new SecureRandom[1 << tl];
		this.threadingmask = prngs.length - 1;
		
		if (MINIMIZE_INITIAL_ENTROPY_CONSUMPTION)
		{
			ChaCha20Random seeder = new ChaCha20Random();
			for (int i = 0; i < prngs.length; ++i)
			{
				byte[] initialseed = new byte[40];
				seeder.nextBytes(initialseed);
				prngs[i] = new ChaCha20Random(initialseed);
			}
		}
		else
		{
			for (int i = 0; i < prngs.length; ++i)
			{
				prngs[i] = new ChaCha20Random();
			}
		}
	}
	
	/**
	 *  Creates the wrapper.
	 */
//	public SecureThreadedRandom(int threadinglevel)
//	{
//		threadinglevel = Math.min(Math.abs(threadinglevel), 31);
//		prngs = new SecureRandom[1 << threadinglevel];
//		this.threadingmask = prngs.length - 1;
//		for (int i = 0; i < prngs.length; ++i)
//		{
//			prngs[i] = new ChaCha20Random();
//		}
//	}
	
	/**
	 *  Wrapper method.
	 */
	public boolean nextBoolean()
	{
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		SecureRandom r = prngs[(int)(Thread.currentThread().getId() & threadingmask)];
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
		int rid = (int)(Thread.currentThread().getId() & threadingmask);
		SecureRandom r = prngs[rid];
		synchronized(r)
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
