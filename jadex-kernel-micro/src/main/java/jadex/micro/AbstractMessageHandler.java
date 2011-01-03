package jadex.micro;

import jadex.bridge.IInternalAccess;
import jadex.bridge.MessageType;
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
	
	/** Flag if should be removed. */
	protected boolean remove;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter filter)
	{
		this(filter, true);
	}
	
	/**
	 *  Create a new message handler.
	 */
	public AbstractMessageHandler(IFilter filter, boolean remove)
	{
		this.filter = filter;
		this.remove = remove;
	}
	
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter getFilter()
	{
		return filter;
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
	 *  Handle the message.
	 */
	public abstract void handleMessage(IInternalAccess ia, Map msg, MessageType type);
}
