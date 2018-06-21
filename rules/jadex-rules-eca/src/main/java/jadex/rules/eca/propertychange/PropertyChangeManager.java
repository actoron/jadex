package jadex.rules.eca.propertychange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.IEvent;

/**
 *  Basic property change manager w/o java bean support. Works on android, too.
 */
public class PropertyChangeManager
{
	/** The event list. */
	protected List<IEvent> events;
	
	/** 
	 * The property change listeners. 
	 * Listener type must be object, because java.beans.PropertyChangeListener/jadex.commons.beans.PropertyChangeListener don't share an interface
	 */
	protected Map<Object, Map<IResultCommand<IFuture<Void>, PropertyChangeEvent>, Object>> pcls;
//	protected Map<Object, Map<IResultCommand<IFuture<Void>, PropertyChangeEvent>, PropertyChangeListener>> pcls;
	
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
			return new PropertyChangeManager();
		} 
		else 
		{
			return new PropertyChangeManagerDesktop();
		}
	}
	
	/**
	 *  Remove a listener from an object.
	 */
	// Listener type must be object, because java.beans.PropertyChangeListener/jadex.commons.beans.PropertyChangeListener don't share an interface
	protected void removePCL(Object object, Object pcl)
	{
		if(pcl!=null)
		{
			try
			{
//				System.out.println(getTypeModel().getName()+": Deregister: "+value+", "+type);						
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "removePropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	
	/**  
	 *  Add a property change listener.
	 */
	public void	addPropertyChangeListener(Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		if(object!=null)
		{
			// Invoke addPropertyChangeListener on value
			try
			{
				Method	meth = getAddMethod(object);
				if(meth!=null)
				{
					if(pcls==null)
						pcls = new IdentityHashMap<Object, Map<IResultCommand<IFuture<Void>, PropertyChangeEvent>, Object>>(); // values may change, therefore identity hash map
					Map<IResultCommand<IFuture<Void>, PropertyChangeEvent>, Object> mypcls = pcls.get(object);
					Object pcl = mypcls==null? null: mypcls.get(eventadder);
					
					if(pcl==null)
					{
						pcl = createPCL(meth, eventadder);
						if(mypcls==null)
						{
							mypcls = new IdentityHashMap<IResultCommand<IFuture<Void>, PropertyChangeEvent>, Object>();
							pcls.put(object, mypcls);
						}
						
						mypcls.put(eventadder, pcl);
					}
					
					meth.invoke(object, new Object[]{pcl});	
				}
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	
	/**
	 *  Deregister a value for observation.
	 *  if its a bean then remove the property listener.
	 */
	public void	removePropertyChangeListener(Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		if(object!=null)
		{
//			System.out.println("deregister ("+cnt[0]+"): "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				Map<IResultCommand<IFuture<Void>, PropertyChangeEvent>, Object> mypcls = pcls.get(object);
				if(mypcls!=null)
				{
					if(eventadder!=null)
					{
						Object pcl = mypcls.remove(eventadder);
						removePCL(object, pcl);
					}
					else
					{
						for(Object pcl: mypcls.values())
						{
							removePCL(object, pcl);
						}
						mypcls.clear();
					}
					if(mypcls.size()==0)
						pcls.remove(object);
				}
			}
		}
	}
	
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
	 *  Create a listener.
	 */
	protected Object createPCL(Method meth, final IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
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

	/**
	 *  Get listener add method
	 */
	protected Method	getAddMethod(Object object)
	{
		// Do not use Class.getMethod (slow).
		Method	meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", PCL);
		return meth;
	}
}
