package jadex.commons.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 *  Factory class for obtaining a thread pool.
 */
public class ThreadPoolFactory
{
	//-------- constants --------
	
	/** The standard (1.4 compliant) thread pool implementation. */
	public static final String	THREADPOOL_STANDARD	= "jadex.commons.concurrent.ThreadPool";

	/** The java 5.0 thread pool implementation. */
	public static final String	THREADPOOL_JAVA5	= "jadex.commons.concurrent.java5.JavaThreadPool";

	//-------- attributes --------
	
	/** The thread pool instance. */
//	protected static IThreadPool	instance;
	
	/** The threadpools per name. */
	protected static final Map threadpools;
	
	//-------- methods --------
	
	static
	{
		threadpools = new HashMap();
	}
	
	/**
	 *  Get the global thread pool instance.
	 *  @return The global thread pool.
	 */
	public static synchronized IThreadPool	getThreadPool(String name)
	{
		IThreadPool ret = (IThreadPool)threadpools.get(name);
		if(ret==null)
		{
			ret = createThreadPool();
			threadpools.put(name, ret);
//			System.out.println("Created threadpool: "+name+" "+ret);
		}
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Create a local thread pool.
	 *  Can be used if a separate pool of threads independent from the global thread pool is required.
	 *  @return A new local thread pool.
	 */
	public static IThreadPool	createThreadPool()
	{
		IThreadPool	instance;
		try
		{
			try
			{
				instance	= (IThreadPool)Class.forName(THREADPOOL_JAVA5).newInstance();
			}
			catch(Exception e)
//			catch(Throwable e)
			{
//				e.printStackTrace();
				instance	= (IThreadPool)Class.forName(THREADPOOL_STANDARD).newInstance();
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not create thread pool.");
		}

		return instance;
	}
}
