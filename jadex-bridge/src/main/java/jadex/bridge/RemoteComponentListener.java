package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *  A listener to be installed remotely in a component.
 *  Events are collected and sent to a remote change listener in bulk events.
 */
public class RemoteComponentListener implements IComponentListener
{
	//-------- constants --------
	
	// todo: make configurable.
	protected static final long UPDATE_DELAY = 100;	
	
	/** Maximum number of events per delay period. */
	// todo: make configurable.
	protected static final int MAX_EVENTS = 50;
	
	//-------- attributes --------
	
	/** The component instance. */
	protected IExternalAccess access;
	
	/** The change listener (proxy) to be informed about important changes. */
	protected IComponentListener listener;
	
	/** The added elements (if any). */
	protected LinkedHashSet	added;
	
	/** The changed elements (if any). */
	protected LinkedHashSet	changed;
	
	/** The removed elements (if any). */
	protected LinkedHashSet	removed;
	
	/** The listed occurrences (if any). */
	protected List occurred;
	
	/** The flag if scheduled. */
	protected boolean scheduled;
	
	//-------- constructs --------
	
	/**
	 *  Create a BDI listener.
	 */
	public RemoteComponentListener(IExternalAccess access, IComponentListener listener)
	{
		this.access = access;
		this.listener = listener;
		this.added	= new LinkedHashSet();
		this.removed = new LinkedHashSet();
		this.changed = new LinkedHashSet();
		this.occurred = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Returns an event filter, indicating which events
	 *  get passed to the eventOccured() method.
	 *  @return The event filter.
	 */
	public IFilter getFilter()
	{
		return listener.getFilter();
	}
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture eventOccured(IComponentChangeEvent event)
	{
		occurrenceAppeared(event);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the listener.
	 *  @return The listener.
	 */
	public IComponentListener getListener()
	{
		return listener;
	}

	/**
	 *  An element was added.
	 */
	public void	elementAdded(IComponentChangeEvent event)
	{
		Entry e = new Entry(event);
		if(!removed.remove(e))
		{
			changed.remove(e);
			added.add(e);
		}
		
		schedule();
	}
	
	/**
	 *  An element was removed.
	 */
	public void	elementRemoved(IComponentChangeEvent event)
	{
		Entry e = new Entry(event);
		if(!added.remove(e))
		{
			changed.remove(e);
			removed.add(e);
		}
		
		schedule();
	}
	
	/**
	 *  An element was changed.
	 */
	public void	elementChanged(IComponentChangeEvent event)
	{
		Entry e = new Entry(event);
		if(!removed.remove(e))
		{
			if(added.remove(e))
			{
				// Replace added element.
				added.add(e);
			}
			else
			{
				// Hack!!! Remove before add, because set does not replace.
				changed.remove(e);
				changed.add(e);
			}
		}
		
		schedule();
	}
	
	/**
	 *  An occurrence appeared.
	 *  @param type	The occurrence type used as prefix for the event (e.g. use 'step' for 'step_occurred' events). 
	 *  @param value	The occurrence value (must be transferable).
	 */
	public void	occurrenceAppeared(IComponentChangeEvent event)
	{
		Entry e = new Entry(event);
		occurred.add(e);
		
		schedule();
	}
	
	/**
	 * 
	 */
	protected void schedule()
	{
		// Local step: no XML classname required.
		if(!scheduled && access!=null &&
			(!removed.isEmpty() || !added.isEmpty() || !changed.isEmpty() || !occurred.isEmpty()))
		{
			scheduled = true;
			access.scheduleImmediate(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						List events	= new ArrayList();
						scheduled = false;
						addEvents(events, removed);
						addEvents(events, added);
						addEvents(events, changed);
						addEvents(events, occurred);
						
						if(!events.isEmpty())
						{
							IComponentChangeEvent event = events.size()==1 ? (ComponentChangeEvent)events.get(0)
								: new BulkComponentChangeEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()]));
							
							listener.eventOccured(event).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
								{
//									System.out.println("update succeeded: "+desc);
									schedule();
								}
								public void exceptionOccurred(Exception exception)
								{
//									System.err.println("update not succeeded: "+exception);
//									exception.printStackTrace();
									if(access!=null)
									{
//										System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
										try
										{
											System.out.println("todo: dispose: "+this);
//											dispose();
										}
										catch(RuntimeException e)
										{
//											System.out.println("Listener already removed: "+id);
										}
										access = null;	// Set to null to avoid multiple removal due to delayed errors. 
									}
								}
							});
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return IFuture.DONE;
				}
			}, UPDATE_DELAY);
		}
	}

	/**
	 * 
	 */
	protected void addEvents(List results, Collection source)
	{
		if(!source.isEmpty())
		{
			for(Iterator it=source.iterator(); results.size()<MAX_EVENTS && it.hasNext(); )
			{
				Entry e	= (Entry)it.next();
				it.remove();
				results.add(e.getEvent());
			}
		}
	}
	
	/**
	 *  Test equality.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = obj instanceof RemoteComponentListener;
		if(ret)
		{
			ret = ((RemoteComponentListener)obj).getListener().equals(listener);
		}
		return ret;
	}
	
	/**
	 *  Hash code.
	 */
	public int hashCode()
	{
		return 31 + listener.hashCode();
	}
	
	/**
	 * 
	 */
	public static class Entry
	{
		/** The event. */
		protected IComponentChangeEvent event;
		
		/**
		 * 
		 */
		public Entry(IComponentChangeEvent event)
		{
			this.event = event;
		}
		
		/**
		 *  Get the event.
		 *  @return The event.
		 */
		public IComponentChangeEvent getEvent()
		{
			return event;
		}

		/**
		 *  Test equality based on id.
		 */
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if(obj instanceof Entry)
			{
				Entry other = (Entry)obj;
				String sn1 = getEvent().getSourceName();
				String sn2 = other.getEvent().getSourceName();
				if(sn1!=null && sn2!=null)
				{
					ret = sn1.equals(sn2);
				}
				else
				{
					ret = getEvent().equals(other.getEvent());
				}
			}
			return ret;
		}
		
		/**
		 *  Hash code based on id.
		 */
		public int hashCode()
		{
			String sn = getEvent().getSourceName();
			return 31 + sn!=null? sn.hashCode(): getEvent().hashCode();
		}
	}
	
}
