package jadex.bytecode.vmhacks;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *  This class abuses the java.util.logging.* API to establish a
 *  VM-wide object store. 
 *
 */
public final class LoggerFilterStore extends ArrayList<Object> implements Filter
{
	private static final long serialVersionUID = -1120016223808401812L;

	/** Hold the logger to prevent GC, weak-referenced globally. */
	protected static Logger instance = null;
	
	private ArrayList<Object> realstore;
	
	/**
	 *  Creates the store.
	 */
	public LoggerFilterStore()
	{
		super(0);
		realstore = new ArrayList<Object>();
		realstore.add(new LinkedBlockingQueue<Object>());
		realstore.add(new ClassStore());
	}
	
	/** Override */
	public Object get(int index)
	{
		return realstore.get(index);
	}

	/**
	 *  Unused
	 */
	public boolean isLoggable(LogRecord record)
	{
		return false;
	}
	
	/**
	 *  Injects the store.
	 */
	public static final void inject()
	{
		if (instance == null)
		{
			synchronized(LoggerFilterStore.class)
			{
				if (instance == null)
				{
					// Must hold instance, global loggers are weakly-referenced.
					instance = Logger.getLogger(String.valueOf(SecurityProviderStore.ID));
					if (instance.getFilter() == null)
					{
						LoggerFilterStore fs = new LoggerFilterStore();
						instance.setFilter(fs);
					}
				}
			}
		}
	}
	
	/**
	 *  Returns a store object.
	 *  
	 *  @param i Store object index.
	 *  @param clazz Class marker.
	 *  @return The object.
	 */
	@SuppressWarnings("unchecked")
	public static final ArrayList<Object> getStore()
	{
		Logger lfs = Logger.getLogger(String.valueOf(SecurityProviderStore.ID));
		return (ArrayList<Object>) lfs.getFilter();
	}
}
