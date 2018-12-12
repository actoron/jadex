package jadex.bdiv3.runtime.wrappers;

import java.util.Set;

import jadex.bdiv3.model.MElement;
import jadex.bridge.IInternalAccess;
import jadex.rules.eca.EventType;

/**
 * 
 */
public class SetWrapper<T> extends jadex.commons.collection.wrappers.SetWrapper<T>
{
	/** The event publisher. */
	protected IEventPublisher publisher;
	
	/**
	 *  Create a new set wrapper.
	 */
	public SetWrapper(Set<T> delegate, IInternalAccess agent, 
		String addevent, String remevent, String changeevent, MElement mbel)
	{
		this(delegate, agent, new EventType(addevent), new EventType(remevent), new EventType(changeevent), mbel);
	}
	
	/**
	 *  Create a new set wrapper.
	 */
	public SetWrapper(Set<T> delegate, IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MElement mbel)
	{
		super(delegate);
		if(agent!=null)
			this.publisher = new EventPublisher(agent, addevent, remevent, changeevent, mbel);
		else
			this.publisher = new InitEventPublisher(delegate, addevent, remevent, changeevent, mbel);

		
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
	protected void entryAdded(T value, int index)
	{
		publisher.entryAdded(value, index);
	}
	
	/**
	 *  An entry was removed from the collection.
	 */
	protected void entryRemoved(T value, int index)
	{
		publisher.entryRemoved(value, index);
	}
	
	/**
	 *  An entry was changed in the collection.
	 */
	protected void entryChanged(T oldvalue, T newvalue, int index)
	{
		publisher.entryChanged(oldvalue, newvalue, index);
	}
}
