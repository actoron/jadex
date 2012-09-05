package jadex.micro;

import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;

import java.util.Map;

/**
 *  Abstract message handler.
 */
public abstract class AbstractMessageHandler implements IMessageHandler
{
	//-------- attributes --------
	
	/** The filter. */
	protected IFilter filter;
	
	/** The timeout. */
	protected long timeout;
	
	/** Flag if should be removed. */
	protected boolean remove;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler()
	{
		this(-1, true);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(long timeout)
	{
		this(timeout, true);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(long timeout, boolean remove)
	{
		this(null, timeout, remove);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter filter)
	{
		this(filter, -1, true);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter filter, long timeout)
	{
		this(filter, timeout, true);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter filter, long timeout, boolean remove)
	{
		this.filter = filter;
		this.timeout = timeout;
		this.remove = remove;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter getFilter()
	{
		return filter;
	}
	
	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter filter)
	{
		this.filter = filter;
	}

	/**
	 *  Test if handler should be removed.
	 *  @return True if it should be removed. 
	 */
	public boolean isRemove()
	{
		return remove;
	}
	
	/**
	 *  Set the remove.
	 *  @param remove The remove to set.
	 */
	public void setRemove(boolean remove)
	{
		this.remove = remove;
	}
	
	/**
	 *  Get the timeout.
	 *  @return the timeout.
	 */
	public long getTimeout()
	{
		return timeout;
	}

	/**
	 *  Set the timeout.
	 *  @param timeout The timeout to set.
	 */
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}

	/**
	 *  Timeout occurred.
	 */
	public void timeoutOccurred() 
	{ 
	}
	
	/**
	 *  Handle the message.
	 */
	public abstract void handleMessage(Map<String, Object> msg, MessageType type);
}
