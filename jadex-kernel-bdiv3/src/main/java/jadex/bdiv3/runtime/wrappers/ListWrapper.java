package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.model.MBelief;
import jadex.bridge.IInternalAccess;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.EventType;

import java.util.List;

/**
 * 
 */
public class ListWrapper<T> extends jadex.commons.collection.wrappers.ListWrapper<T> 
{
	/** The event publisher. */
	protected EventPublisher publisher;

	/**
	 *  Create a new list wrapper.
	 */
	public ListWrapper(List<T> delegate, IInternalAccess agent, 
		String addevent, String remevent, String changeevent, MBelief mbel)
	{
		this(delegate, agent, new EventType(addevent), new EventType(remevent), new EventType(changeevent), mbel);
	}
	
	/**
	 *  Create a new list wrapper.
	 */
	public ListWrapper(List<T> delegate, IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MBelief mbel)
	{
		super(delegate);
		this.publisher = new EventPublisher(agent, addevent, remevent, changeevent, mbel);
	}
	
	/**
	 *  An entry was added to the collection.
	 */
	protected void entryAdded(T value, int index)
	{
//		unobserveValue(ret);
		publisher.observeValue(value);
		publisher.getRuleSystem().addEvent(new Event(publisher.getAddEvent(), new ChangeInfo<T>(value, null, index>-1? Integer.valueOf(index): null)));
		publisher.publishToolBeliefEvent();
	}
	
	/**
	 *  An entry was removed from the collection.
	 */
	protected void entryRemoved(T value, int index)
	{
		publisher.unobserveValue(value);
//		observeValue(value);
		publisher.getRuleSystem().addEvent(new Event(publisher.getRemEvent(), new ChangeInfo<T>(value, null, index>-1? Integer.valueOf(index): null)));
		publisher.publishToolBeliefEvent();
	}
	
	/**
	 *  An entry was changed in the collection.
	 */
	protected void entryChanged(T oldvalue, T newvalue, int index)
	{
		publisher.unobserveValue(oldvalue);
		publisher.observeValue(newvalue);
		publisher.getRuleSystem().addEvent(new Event(publisher.getRemEvent(), new ChangeInfo<T>(newvalue, oldvalue,  index>-1? Integer.valueOf(index): null)));
		publisher.publishToolBeliefEvent();
	}
}