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
	/**
	 *  ID
	 */
	public static final int ID = 23070273;
	
	/**
	 * 
	 */
	public static final long serialVersionUID = ID;
	
	protected static Logger instance = null;
	
	/**
	 *  Creates the store.
	 */
	public LoggerFilterStore()
	{
		add(new LinkedBlockingQueue<Object>());
		add(new ClassStore());
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
					instance = Logger.getLogger(String.valueOf(ID));
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
		Logger lfs = Logger.getLogger(String.valueOf(ID));
		return (ArrayList<Object>) lfs.getFilter();
	}
}
