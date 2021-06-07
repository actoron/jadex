package jadex.bdiv3.runtime.wrappers;

import java.util.Map;

import jadex.bdiv3.model.MElement;
import jadex.bridge.IInternalAccess;
import jadex.rules.eca.EventType;

/**
 * 
 */
public class MapWrapper<T, E> extends jadex.commons.collection.wrappers.MapWrapper<T, E>
{
	/** The event publisher. */
	protected IEventPublisher publisher;
	
	/**
	 *  Create a new set wrapper.
	 */
	public MapWrapper(Map<T, E> delegate, IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MElement mbel)
	{
		super(delegate);
		if(agent!=null)
			this.publisher = new EventPublisher(agent, addevent, remevent, changeevent, mbel);
		else
			this.publisher = new InitEventPublisher(delegate, addevent, remevent, changeevent, mbel);

		
		for(Map.Entry<T,E> entry: delegate.entrySet())
		{
			publisher.entryAdded(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * 
	 */
	public void setAgent(IInternalAccess agent)
	{
		if(publisher instanceof InitEventPublisher)
		{
			InitEventPublisher pub = (InitEventPublisher)publisher;
			this.publisher = new EventPublisher(agent, pub.addevent, pub.remevent, pub.changeevent, pub.melement);
		}
	}
	
	/**
	 * 
	 */
	public boolean isInitWrite()
	{
		return publisher instanceof InitEventPublisher;
	}
	
	/**
	 *  An entry was added to the map.
	 */
	protected void	entryAdded(T key, E value)
	{
		publisher.entryAdded(key, value);
	}
	
	/**
	 *  An entry was removed from the map.
	 */
	protected void	entryRemoved(T key, E value)
	{	
		publisher.entryRemoved(key, value);
	}
	
	/**
	 *  An entry was changed in the map.
	 */
	protected void	entryChanged(T key, E oldvalue, E newvalue)
	{
		publisher.entryChanged(key, oldvalue, newvalue);
	}
}
