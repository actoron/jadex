package jadex.rules.eca.propertychange;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.IEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract class to provide a Factory Method and common methods for
 * managing of PropertyChangeEvents/Listeners.
 */
public abstract class PropertyChangeManager
{
	/** The event list. */
	protected List<IEvent> events;
	
	/** 
	 * The property change listeners. 
	 * Value type must be object, because java.beans/jadex.commons.beans don't share an interface
	 */
	protected Map<Object, Map<Object, Object>> pcls;
	
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static Class<?>[]	PCL	= new Class[]{jadex.commons.beans.PropertyChangeListener.class};

	/** Protected Constructor to prevent direct instantiation **/
	protected PropertyChangeManager()
	{
		this.events = new ArrayList<IEvent>();
	}
	
	/** Returns a new PropertyChangeManager instance **/ 
	public static PropertyChangeManager createInstance()
	{
		if(SReflect.isAndroid()) 
		{
			return new PropertyChangeManagerAndroid();
		} 
		else 
		{
			return new PropertyChangeManagerDesktop();
		}
	}
	
	/**  
	 *  Add a property change listener.
	 */
	public abstract void addPropertyChangeListener(Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder);
	
	/**
	 *  Deregister a value for observation.
	 *  if its a bean then remove the property listener.
	 */
	public abstract void removePropertyChangeListener(Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder);
	
	/**
	 *  Add an event.
	 */
	public void addEvent(IEvent event)
	{
		events.add(event);
	}

	/**
	 *  Test if events are available.
	 *  @return True, if has events.
	 */
	public boolean hasEvents()
	{
		return events.size()>0;
	}

	/**
	 *  Remove an event.
	 *  @param index The index.
	 */
	public IEvent removeEvent(int index)
	{
		return events.remove(index);
	}
	
	/**
	 *  Get the number of events. 
	 *  @return The number of events.
	 */
	public int getSize()
	{
		return events.size();
	}
	
	// ---- Helper -----
	
	/**
	 *  Create a property change listener.
	 *  @param eventadder The event adder element.
	 *  @return
	 */
	protected jadex.commons.beans.PropertyChangeListener createPCL(final IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		return new jadex.commons.beans.PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				// todo: problems:
				// - may be called on wrong thread (-> synchronizator)
				// - how to create correct event with type and value

				if(eventadder!=null)
				{
					eventadder.execute(evt).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void event)
						{
//							if(event!=null)
//							{
//								addEvent(event);
//							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Event creator had exception: "+exception);
						}
					});
				}
				else
				{
					Event event = new Event(evt.getPropertyName(), new ChangeInfo<Object>(evt.getNewValue(), evt.getOldValue(), null));
					addEvent(event);
				}
			}
		};
	}
	
}
