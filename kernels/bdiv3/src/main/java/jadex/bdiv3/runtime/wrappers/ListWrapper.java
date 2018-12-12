package jadex.bdiv3.runtime.wrappers;

import java.util.List;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.model.MElement;
import jadex.bridge.IInternalAccess;
import jadex.rules.eca.EventType;

/**
 * 
 */
public class ListWrapper<T> extends jadex.commons.collection.wrappers.ListWrapper<T> 
{
	/** The event publisher. */
	protected IEventPublisher publisher;

	/**
	 *  Create a new list wrapper.
	 */
	public ListWrapper(List<T> delegate, IInternalAccess agent, 
		String addevent, String remevent, String changeevent, MElement melem)
	{
		this(delegate, agent, new EventType(addevent), new EventType(remevent), new EventType(changeevent), melem);
	}
	
	/**
	 *  Create a new list wrapper.
	 */
	public ListWrapper(List<T> delegate, IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MElement melem)
	{
		super(delegate);
		if(agent!=null)
			this.publisher = new EventPublisher(agent, addevent, remevent, changeevent, melem);
		else
			this.publisher = new InitEventPublisher(delegate, addevent, remevent, changeevent, melem);
			
		int	i=0;
		for(T entry: delegate)
		{
			publisher.entryAdded(entry, i++);
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
	 *  An entry was added to the collection.
	 */
	public void entryAdded(T value, int index)
	{
		publisher.entryAdded(value, index);
	}
	
	/**
	 *  An entry was removed from the collection.
	 */
	public void entryRemoved(T value, int index)
	{
		publisher.entryRemoved(value, index);
	}
	
	/**
	 *  An entry was changed in the collection.
	 */
	public void entryChanged(T oldvalue, T newvalue, int index)
	{
		publisher.entryChanged(oldvalue, newvalue, index);
	}
}