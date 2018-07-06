/**
 * 
 */
package org.activecomponents.udp.asymciphers;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import org.activecomponents.udp.IKeyProvider;
import org.activecomponents.udp.IThreadExecutor;
import org.activecomponents.udp.SUdpUtil;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;

/**
 *
 */
public class RsaKeyProvider implements IKeyProvider
{
	/** RSA exponent. */
	protected static final BigInteger EXP = new BigInteger("65537");
	
	/** The default key size. */
	protected static final int DEFAULT_KEY_SIZE = 4096;
	
	/** The default maximum number of pre-gen keys. */
	protected static final int DEFAULT_MAX_KEYS = 10;
	
	/** Generating keys in parallel. */
	protected static final boolean PARALLEL_MODE = true;
	
	/** Pre-generated keys. */
	protected BlockingQueue<AsymmetricCipherKeyPair> pregenkeys;
	
	/** Running flag. */
	protected volatile boolean running;
	
	/** Barrier for coordinating shutdown. */
	protected CyclicBarrier shutdownbarrier;
	
	/** Generation task. */
	protected Runnable task;
	
	/**
	 *  Creates the provider.
	 */
	public RsaKeyProvider()
	{
		this(DEFAULT_KEY_SIZE);
	}
	
	/**
	 *  Creates the provider.
	 */
	public RsaKeyProvider(int keysize)
	{
		this(keysize, DEFAULT_MAX_KEYS);
	}
	
	/**
	 *  Creates the provider.
	 */
	public RsaKeyProvider(final int keysize, final int maxkeys)
	{
		pregenkeys = new LinkedBlockingQueue<AsymmetricCipherKeyPair>();
		
		task = new Runnable()
		{
			public void run()
			{
				RSAKeyPairGenerator gen = new RSAKeyPairGenerator();
				RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(EXP, SUdpUtil.getSecRandom(), keysize, 128);
				gen.init(params);
				while (running)
				{
					while (pregenkeys.size() < maxkeys)
					{
						AsymmetricCipherKeyPair keypair = gen.generateKeyPair();
						try
						{
							pregenkeys.put(keypair);
//							System.out.println("Key generated, queue length: " + pregenkeys.size());
						}
						catch (InterruptedException e)
						{
						}
					}
					
					synchronized(pregenkeys)
					{
						if (pregenkeys.size() >= maxkeys)
						{
							try
							{
								pregenkeys.wait();
							}
							catch (InterruptedException e)
							{
							}
						}
					}
				}
				try
				{
					shutdownbarrier.await();
				}
				catch (Exception e)
				{
				}
			}
		};
	}
	
	/**
	 *  Starts the key generation. 
	 */
	public void start(IThreadExecutor texec)
	{
		running = true;
		int maxthreads = 1;
		if (PARALLEL_MODE)
			maxthreads = Runtime.getRuntime().availableProcessors() + 1;
		shutdownbarrier = new CyclicBarrier(maxthreads + 1);
		for (int i = 0; i < maxthreads; ++i)
		{
			texec.run(task);
		}
	}
	
	/**
	 *  Stops the key generation. 
	 */
	public void stop()
	{
		running = false;
		synchronized(pregenkeys)
		{
			pregenkeys.notifyAll();
		}
		try
		{
			shutdownbarrier.await();
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 *  Returns a random key pair.
	 *  
	 *  @return Key pair.
	 */
	public AsymmetricCipherKeyPair getKeyPair()
	{
		if (!running)
		{
			throw new IllegalStateException("Key provider not started.");
		}
		
		AsymmetricCipherKeyPair ret = null;
		while (ret == null)
		{
			try
			{
				ret = pregenkeys.take();
			}
			catch (InterruptedException e)
			{
			}
		}
		synchronized(pregenkeys)
		{
			pregenkeys.notifyAll();
		}
		return ret;
	}
}
