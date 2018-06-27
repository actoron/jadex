package jadex.commons.security.random;

import java.security.SecureRandom;

public class SynchronizedSecureRandomWrapper extends SecureRandom
{

	/** Serial */
	private static final long serialVersionUID = -2816942884190634169L;
	
	protected SecureRandom delegate;
	
	public SynchronizedSecureRandomWrapper(SecureRandom delegate)
	{
		this.delegate = delegate;
	}
	
	/** Delegation */
	public synchronized byte[] generateSeed(int numBytes)
	{
		return delegate.generateSeed(numBytes);
	}
	
	/** Delegation */
	public synchronized String getAlgorithm()
	{
		return delegate.getAlgorithm();
	}
	
	/** Delegation */
	public synchronized void nextBytes(byte[] bytes)
	{
		delegate.nextBytes(bytes);
	}
	
	/** Delegation */
	public synchronized void setSeed(byte[] seed)
	{
		if (delegate != null)
			delegate.setSeed(seed);
	}
	
	/** Delegation */
	public synchronized void setSeed(long seed)
	{
		if (delegate != null)
			delegate.setSeed(seed);
	}
	
	/** Delegation */
	public synchronized boolean nextBoolean()
	{
		return delegate.nextBoolean();
	}
	
	/** Delegation */
	public synchronized double nextDouble()
	{
		return delegate.nextDouble();
	}
	
	/** Delegation */
	public synchronized float nextFloat()
	{
		return delegate.nextFloat();
	}
	
	/** Delegation */
	public synchronized double nextGaussian()
	{
		return delegate.nextGaussian();
	}
	
	/** Delegation */
	public synchronized int nextInt()
	{
		return delegate.nextInt();
	}
	
	/** Delegation */
	public synchronized int nextInt(int bound)
	{
		return delegate.nextInt(bound);
	}
	
	/** Delegation */
	public synchronized long nextLong()
	{
		return delegate.nextLong();
	}
}
