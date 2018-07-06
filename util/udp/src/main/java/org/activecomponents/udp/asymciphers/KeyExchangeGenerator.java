package org.activecomponents.udp.asymciphers;

import java.lang.reflect.Constructor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import org.activecomponents.udp.IThreadExecutor;

/**
 *  Key exchange generator which can be used to pre-generate exchanges in background.
 *
 */
public class KeyExchangeGenerator
{
	/** The default target number of pre-gen key exchanges. */
	protected static final int DEFAULT_TARGET_PREGEN = 10;
	
	/** Pre-generated exchange mechanisms. */
	protected BlockingQueue<IKeyExchange> pregenex;
	
	/** Running flag. */
	protected volatile boolean running;
	
	/** Barrier for coordinating shutdown. */
	protected CyclicBarrier shutdownbarrier;
	
	/** Constructor for the key exchange */
	protected Constructor<?> con;
	
	/** Generation task. */
	protected Runnable task;
	
	public KeyExchangeGenerator(String exchangeclass)
	{
		this(exchangeclass, DEFAULT_TARGET_PREGEN);
	}
	
	/**
	 *  Creates a new key exchange generator.
	 * 
	 *  @param exchangeclass Class used for key exchange.
	 *  @param targetkeys Target number of pre-generated exchanges.
	 */
	public KeyExchangeGenerator(String exchangeclass, final int targetkeys)
	{
		this.pregenex = new LinkedBlockingQueue<IKeyExchange>();
		try
		{
			Class<?> xco = Class.forName(exchangeclass);
			con = xco.getConstructor(new Class<?>[0]);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		task = new Runnable()
		{
			public void run()
			{
				while (running)
				{
					while (pregenex.size() < targetkeys)
					{
						try
						{
							IKeyExchange ex = (IKeyExchange) con.newInstance(new Object[0]);
							pregenex.put(ex);
						}
						catch (Exception e)
						{
						}
					}
					
					synchronized(pregenex)
					{
						if (pregenex.size() >= targetkeys)
						{
							try
							{
								pregenex.wait();
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
	 *  Starts the background key exchange generation. 
	 */
	public void start(IThreadExecutor texec)
	{
		running = true;
		int maxthreads = Runtime.getRuntime().availableProcessors() + 1;
		shutdownbarrier = new CyclicBarrier(maxthreads + 1);
		for (int i = 0; i < maxthreads; ++i)
		{
			texec.run(task);
		}
	}
	
	/**
	 *  Stops the background key exchange generation. 
	 */
	public void stop()
	{
		running = false;
		synchronized(pregenex)
		{
			pregenex.notifyAll();
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
	 *  Returns a new key exchange mechanism.
	 *  
	 *  @return Key exchange mechanism.
	 */
	public IKeyExchange getKeyExchange()
	{
		IKeyExchange ret = null;
		
		if (!running)
		{
			try
			{
				ret = (IKeyExchange) con.newInstance(new Object[0]);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			while (ret == null)
			{
				try
				{
					ret = pregenex.take();
				}
				catch (InterruptedException e)
				{
				}
			}
			synchronized(pregenex)
			{
				pregenex.notifyAll();
			}
		}
		
		return ret;
	}
}
