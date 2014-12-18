package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.model.MBelief;
import jadex.bridge.IInternalAccess;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.EventType;

import java.util.Map;

/**
 * 
 */
public class MapWrapper<T, E> extends jadex.commons.collection.wrappers.MapWrapper<T, E>
{
	/** The event publisher. */
	protected EventPublisher publisher;
	
	/**
	 *  Create a new set wrapper.
	 */
	public MapWrapper(Map<T, E> delegate, IInternalAccess agent, 
		String addevent, String remevent, String changeevent, MBelief mbel)
	{
		this(delegate, agent, new EventType(addevent), new EventType(remevent), new EventType(changeevent), mbel);
	}
	
	/**
	 *  Create a new set wrapper.
	 */
	public MapWrapper(Map<T, E> delegate, IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MBelief mbel)
	{
		super(delegate);
		this.publisher = new EventPublisher(agent, addevent, remevent, changeevent, mbel);
	}
	
	/**
	 *  An entry was added to the map.
	 */
	protected void	entryAdded(T key, E value)
	{
		publisher.observeValue(value);
		publisher.getRuleSystem().addEvent(new Event(publisher.getAddEvent(), new ChangeInfo<E>(value, null, key)));
		publisher.publishToolBeliefEvent();
	}
	
	/**
	 *  An entry was removed from the map.
	 */
	protected void	entryRemoved(T key, E value)
	{
		publisher.unobserveValue(value);
		publisher.getRuleSystem().addEvent(new Event(publisher.getRemEvent(), new ChangeInfo<E>(null, value, key)));
		publisher.publishToolBeliefEvent();
	}
	
	/**
	 *  An entry was changed in the map.
	 */
	protected void	entryChanged(T key, E oldvalue, E newvalue)
	{
		publisher.unobserveValue(oldvalue);
		publisher.observeValue(newvalue);
		publisher.getRuleSystem().addEvent(new Event(publisher.getChangeEvent(), new ChangeInfo<E>(newvalue, oldvalue, key)));
		publisher.publishToolBeliefEvent();
	}

}
