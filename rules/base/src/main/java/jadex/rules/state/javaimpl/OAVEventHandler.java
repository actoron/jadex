package jadex.rules.state.javaimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.commons.collection.IdentityHashSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;


/**
 *  This class handles the collection and distribution OAV
 *  events to registered listeners.
 */
public class OAVEventHandler
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The bunch state listeners. */
	protected List listeners;
	
	/** The direct state listeners. */
	protected List directlisteners;
	
	/** The collected change events. */
	protected Set oavevents;

	/** The collected bean events (may be added from external thread). */
	protected Set beanevents;

	/** The objects that have been removed in current change set. */
	protected Set removed_objects;
	
	/** The objects that have been added in current change set. */
	protected Set added_objects;
	
	/** Flag that is only true, while listeners are being notified. */
	protected boolean	notifying;

	//-------- constructors --------
	
	/**
	 *  Create a new OAV event handler.
	 */
	public OAVEventHandler(IOAVState state)
	{
		this.state	= state;
		this.listeners	= new ArrayList();
		this.directlisteners = new ArrayList();
		this.oavevents = new LinkedHashSet();
//		this.oavevents = new CheckedCollection(new LinkedHashSet());
		this.beanevents = Collections.synchronizedSet(new LinkedHashSet());
//		this.beanevents = Collections.synchronizedSet(new CheckedCollection(new LinkedHashSet()));
	}
	
	//-------- state observers --------
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 */
	public void addStateListener(IOAVStateListener listener, boolean bunch)
	{
		if(bunch)
			this.listeners.add(listener);
		else
			this.directlisteners.add(listener);
	}
	
	/**
	 *  Remove a state listener.
	 *  @param listener The state listener.
	 */
	public void removeStateListener(IOAVStateListener listener)
	{
		if(!this.listeners.remove(listener))
			if(!this.directlisteners.remove(listener))
				throw new RuntimeException("Listener not found: "+listener);
	}
	
	/**
	 *  Throw collected events and notify the listeners.
	 */
	public void notifyEventListeners()
	{
		this.notifying	= true;
		if(!beanevents.isEmpty())
		{
			Object[]	abeanevents;
			synchronized(beanevents)
			{
				abeanevents	= beanevents.toArray();
				beanevents.clear();
			}
			for(int i=0; i<abeanevents.length; i++)
			{
				notifyOneEvent(abeanevents[i]);
			}
		}
		
		Object[]	aoavevents	= oavevents.toArray();
		for(int i=0; i<aoavevents.length; i++)
		{
			notifyOneEvent(aoavevents[i]);
		}
		oavevents.clear();
		
		removed_objects	= null;
		added_objects	= null;
		this.notifying	= false;
	}
	
	/**
	 *  Notify one event to all listeners.
	 *  @param evt The event.
	 */
	protected void notifyOneEvent(Object evt)
	{
//		System.out.println("notify: "+evt);
		
		if(evt instanceof OAVObjectAddedEvent)
		{
			OAVObjectAddedEvent event = (OAVObjectAddedEvent)evt;
			if(removed_objects==null || !removed_objects.contains(event.id))
			{
				for(int i=0; i<listeners.size(); i++)
				{
					((IOAVStateListener)listeners.get(i)).objectAdded(event.id, event.type, event.root);
				}
			}
//			else
//			{
//				System.out.println("Ignored added object: "+event.id);
//			}
		}
		else if(evt instanceof OAVObjectRemovedEvent)
		{
			OAVObjectRemovedEvent event = (OAVObjectRemovedEvent)evt;
			if(added_objects==null || !added_objects.contains(event.id))
			{
				for(int i=0; i<listeners.size(); i++)
				{
					((IOAVStateListener)listeners.get(i)).objectRemoved(event.id, event.type);
				}
			}
		}
		else //if(event instanceof OAVObjectModifiedEvent)
		{	
			OAVObjectModifiedEvent event = (OAVObjectModifiedEvent)evt;
			
//			System.out.println("notify: "+event);
			
			if((added_objects==null || !added_objects.contains(event.id))
				&& (removed_objects==null || !removed_objects.contains(event.id)))
			{	
				for(int i=0; i<listeners.size(); i++)
				{
					((IOAVStateListener)listeners.get(i)).objectModified(event.id, event.type, 
						event.attribute, event.oldvalue, event.newvalue);
				}
			}
//			else
//			{
//				System.out.println("Ignored modified object: "+event.id);
//			}
		}
	}
	
	//-------- event methods --------

	/**
	 *  Notification when an attribute value of an object has been set.
	 *  @param id The object id.
	 *  @param type The object type.
	 *  @param attr The attribute type.
	 *  @param oldvalue The oldvalue.
	 *  @param newvalue The newvalue.
	 */
	public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
	{
		if(!listeners.isEmpty())
		{
			if((added_objects==null || !added_objects.contains(id))
				&& (removed_objects==null || !removed_objects.contains(id)))
			{
				OAVObjectModifiedEvent evt = new OAVObjectModifiedEvent(state, id, type, attr, oldvalue, newvalue);
				oavevents.remove(evt); // All events are necessary for external listeners
				oavevents.add(evt);
			}
		}
		
		for(int i=0; i<directlisteners.size(); i++)
			((IOAVStateListener)directlisteners.get(i)).objectModified(id, type, attr, oldvalue, newvalue);		
	}
	
	/**
	 *  Notification when an attribute value of a bean has been set.
	 *  @param bean The bean.
	 *  @param type The object type.
	 *  @param attr The attribute type.
	 *  @param oldvalue The oldvalue.
	 *  @param newvalue The newvalue.
	 */
	public void beanModified(Object bean, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
	{
		synchronized(beanevents)
		{
			OAVObjectModifiedEvent evt = new OAVObjectModifiedEvent(state, bean, type, attr, oldvalue, newvalue);
			if(!listeners.isEmpty())
			{
				beanevents.remove(evt);
				beanevents.add(evt);
			}
			
			for(int i=0; i<directlisteners.size(); i++)
				((IOAVStateListener)directlisteners.get(i)).objectModified(bean, type, attr, oldvalue, newvalue);
		}
	}
	
	/**
	 *  Notification when an object has been added to the state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectAdded(Object id, OAVObjectType type, boolean root)
	{
//		System.out.println("added: "+id+" "+type);
//		Thread.dumpStack();
//		if(type instanceof OAVJavaType && ((OAVJavaType)type).getClazz().getName().indexOf("Wastebin")!=-1)
//		{
//			System.out.println("added: "+id);
//		}
		
		if(!listeners.isEmpty())
		{
			if(removed_objects==null || !removed_objects.contains(id))
			{
				oavevents.add(new OAVObjectAddedEvent(id, type, root));
			}
			else
			{
				removed_objects.remove(id);
			}
			
			if(added_objects==null)
				added_objects	= createIdSet();
			added_objects.add(id);
		}
		
		for(int i=0; i<directlisteners.size(); i++)
			((IOAVStateListener)directlisteners.get(i)).objectAdded(id, type, root);
	}
	
	/**
	 *  Notification when an object has been removed from state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectRemoved(Object id, OAVObjectType type/*, Map content*/)
	{
//		if(type instanceof OAVJavaType && ((OAVJavaType)type).getClazz().getName().indexOf("Wastebin")!=-1)
//		{
//			System.out.println("removed: "+id);
//		}
		
		/*if(id.toString().indexOf("id=Chargingstation")!=-1)
		{
			System.out.println("Removed: "+id);			
			Thread.dumpStack();
		}*/
		
		if(!listeners.isEmpty())
		{
			if(added_objects==null || !added_objects.contains(id))
			{
				oavevents.add(new OAVObjectRemovedEvent(id, type));
			}
			else
			{
				added_objects.remove(id);
			}
			if(removed_objects==null)
				removed_objects	= createIdSet();
			removed_objects.add(id);
		}
		
		for(int i=0; i<directlisteners.size(); i++)
			((IOAVStateListener)directlisteners.get(i)).objectRemoved(id, type);
	}

	/**
	 *  Create a set for holding object ids.
	 */
	protected Set	createIdSet()
	{
		return state.isJavaIdentity() ? (Set)new IdentityHashSet() : new HashSet();
	}
}
