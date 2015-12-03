package jadex.micro;

import java.util.Map;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;

/**
 *  Abstract message handler.
 */
public abstract class AbstractMessageHandler implements IMessageHandler
{
	//-------- attributes --------
	
	/** The filter. */
	protected IFilter<IMessageAdapter> filter;
	
	/** The timeout. */
	protected long timeout;
	
	/** Flag if should be removed. */
	protected boolean remove;
	
	/** The realtime flag. */
	protected boolean realtime; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler()
	{
		this(null, -1, true, false);
	}
		
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter<IMessageAdapter> filter, long timeout, boolean remove, boolean realtime)
	{
		this.filter = filter;
		this.timeout = timeout;
		this.remove = remove;
		this.realtime	= realtime;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter<IMessageAdapter> getFilter()
	{
		return filter;
	}
	
	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter<IMessageAdapter> filter)
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
	 *  Test if handler should be real time.
	 *  @return True if it should be real time. 
	 */
	public boolean isRealtime()
	{
		return realtime;
	}
	
	/**
	 *  Set the real time.
	 *  @param realtime The real time flag to set.
	 */
	public void setRealtime(boolean realtime)
	{
		this.realtime = realtime;
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
