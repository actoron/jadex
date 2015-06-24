package jadex.bridge;

import jadex.bridge.component.IExecutionFeature;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  A listener to be installed remotely in a component.
 *  Events are collected and sent to a remote change listener in bulk events.
 */
public abstract class RemoteChangeListenerHandler
{
	//-------- constants --------
	
	/** The event type suffix for added events. */
	public static final String	EVENT_ADDED	= "_added";
	
	/** The event type suffix for removed events. */
	public static final String	EVENT_REMOVED	= "_removed";
	
	/** The event type suffix for changed events. */
	public static final String	EVENT_CHANGED	= "_changed";
	
	/** The event type suffix for occurred events. */
	public static final String	EVENT_OCCURRED	= "_occurred";
	
	/** The event type for bulk events. */
	public static final String	EVENT_BULK	= "bulk_event";
	
	/** Update delay. */
	// todo: make configurable.
	protected static final long UPDATE_DELAY	= 100;	
	
	/** Maximum number of events per delay period. */
	// todo: make configurable.
	protected static final int MAX_EVENTS	= 400;
	
	//-------- attributes --------
	
	/** The id for remote listener deregistration. */
	protected String	id;
	
	/** The component instance. */
	protected IInternalAccess	instance;
	
	/** The change listener (proxy) to be informed about important changes. */
	protected IRemoteChangeListener	rcl;
	
	/** The added elements (if any). */
	protected MultiCollection<String, Object>	added;
	
	/** The changed elements (if any). */
	protected MultiCollection<String, Object>	changed;
	
	/** The removed elements (if any). */
	protected MultiCollection<String, Object>	removed;
	
	/** The listed occurrences (if any). */
	protected MultiCollection<String, Object>	occurred;
	
	/** The update timer (if any). */
	protected Timer	timer;
	
	/** The flag that the timer has been started. */
	protected boolean	started;
	
	//-------- constructs --------
	
	/**
	 *  Create a BDI listener.
	 */
	public RemoteChangeListenerHandler(String id, IInternalAccess instance, IRemoteChangeListener rcl)
	{
		this.id	= id;
		this.instance	= instance;
		this.rcl	= rcl;
		this.added	= new MultiCollection<String, Object>(new HashMap(), LinkedHashSet.class);
		this.removed	= new MultiCollection<String, Object>(new HashMap(), LinkedHashSet.class);
		this.changed	= new MultiCollection<String, Object>(new HashMap(), LinkedHashSet.class);
		this.occurred	= new MultiCollection<String, Object>();
	}
	
	//-------- methods --------
	
	/**
	 *  An element was added.
	 *  @param type	The element type used as prefix for the event (e.g. use 'goal' for 'goal_added' events). 
	 *  @param value	The element value (must be transferable).
	 */
	public void	elementAdded(String type, Object value)
	{
		if(!removed.containsKey(type) || !removed.getCollection(type).remove(value))
		{
			if(changed.containsKey(type))
				changed.getCollection(type).remove(value);
			
			added.add(type, value);
		}
		
		startTimer();
	}
	
	/**
	 *  An element was removed.
	 *  @param type	The element type used as prefix for the event (e.g. use 'goal' for 'goal_removed' events). 
	 *  @param value	The element value (must be transferable).
	 */
	public void	elementRemoved(String type, Object value)
	{
		if(!added.containsKey(type) || !added.getCollection(type).remove(value))
		{
			if(changed.containsKey(type))
				changed.getCollection(type).remove(value);
			
			removed.add(type, value);
		}
		
		startTimer();
	}
	
	/**
	 *  An element was changed.
	 *  @param type	The element type used as prefix for the event (e.g. use 'goal' for 'goal_changed' events). 
	 *  @param value	The element value (must be transferable).
	 */
	public void	elementChanged(String type, Object value)
	{
		if(!removed.containsKey(type) || !removed.getCollection(type).remove(value))
		{
			if(added.containsKey(type) && added.getCollection(type).remove(value))
			{
				// Replace added element.
				added.add(type, value);
			}
			else
			{
				// Hack!!! Remove before add, because set does not replace.
				changed.getCollection(type).remove(value);
				changed.add(type, value);
			}
		}
		
		startTimer();
	}
	
	/**
	 *  An occurrence appeared.
	 *  @param type	The occurrence type used as prefix for the event (e.g. use 'step' for 'step_occurred' events). 
	 *  @param value	The occurrence value (must be transferable).
	 */
	public void	occurrenceAppeared(String type, Object value)
	{
		occurred.add(type, value);
		
		startTimer();
	}
	
	/**
	 * 
	 */
	protected void startTimer()
	{
		if(!started && instance!=null &&
			(!removed.isEmpty() || !added.isEmpty() || !changed.isEmpty() || !occurred.isEmpty()))
		{
			final IExternalAccess	access	= instance.getExternalAccess();
			if(timer==null)
			{
//				System.out.println("new timer: "+this);
				timer	= new Timer(true);
			}
			
			started	= true;
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					// Local step: no XML classname required.
					access.scheduleStep(new ImmediateComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							try
							{
								List	events	= new ArrayList();
								started	= false;
								if(!removed.isEmpty())
								{
									for(Iterator it=removed.keySet().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										String	type	= (String)it.next();
										for(Iterator it2=removed.getCollection(type).iterator(); events.size()<MAX_EVENTS && it2.hasNext(); )
										{
											events.add(new ChangeEvent(null, type+EVENT_REMOVED, it2.next()));
											it2.remove();
										}
										if(removed.getCollection(type).isEmpty())
										{
											it.remove();
										}
									}
								}
								if(!added.isEmpty())
								{
									for(Iterator it=added.keySet().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										String	type	= (String)it.next();
										for(Iterator it2=added.getCollection(type).iterator(); events.size()<MAX_EVENTS && it2.hasNext(); )
										{
											events.add(new ChangeEvent(null, type+EVENT_ADDED, it2.next()));
											it2.remove();
										}
										if(added.getCollection(type).isEmpty())
										{
											it.remove();
										}
									}
								}
								if(!changed.isEmpty())
								{
									for(Iterator it=changed.keySet().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										String	type	= (String)it.next();
										for(Iterator it2=changed.getCollection(type).iterator(); events.size()<MAX_EVENTS && it2.hasNext(); )
										{
											events.add(new ChangeEvent(null, type+EVENT_CHANGED, it2.next()));
											it2.remove();
										}
										if(changed.getCollection(type).isEmpty())
										{
											it.remove();
										}
									}
								}
								if(!occurred.isEmpty())
								{
									for(Iterator it=occurred.keySet().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										String	type	= (String)it.next();
										for(Iterator it2=occurred.getCollection(type).iterator(); events.size()<MAX_EVENTS && it2.hasNext(); )
										{
											events.add(new ChangeEvent(null, type+EVENT_OCCURRED, it2.next()));
											it2.remove();
										}
										if(occurred.getCollection(type).isEmpty())
										{
											it.remove();
										}
									}
								}
								
								if(!events.isEmpty())
								{
									ChangeEvent	event	= events.size()==1 ? (ChangeEvent)events.get(0)
										: new ChangeEvent(null, EVENT_BULK, events);
									IFuture	fut	= rcl.changeOccurred(event);
									IInternalAccess	tmp	= instance;	// instance might be set to null concurrently.
									if(tmp!=null)
									{
										fut.addResultListener(tmp.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
		//										System.out.println("update succeeded: "+desc);
												startTimer();
											}
											public void exceptionOccurred(Exception exception)
											{
	//											System.err.println("update not succeeded: "+exception);
	//											exception.printStackTrace();
												if(instance!=null)
												{
		//											System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
													try
													{
														dispose();
													}
													catch(RuntimeException e)
													{
		//												System.out.println("Listener already removed: "+id);
													}
													instance	= null;	// Set to null to avoid multiple removal due to delayed errors. 
												}
											}
										}));
									}
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							return IFuture.DONE;
						}
					});						
				}
			}, UPDATE_DELAY);
		}
	}
	
	/**
	 *  Test equality based on id.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof RemoteChangeListenerHandler && ((RemoteChangeListenerHandler)obj).id.equals(id);
	}
	
	/**
	 *  Hash code based on id.
	 */
	public int hashCode()
	{
		return 31 + id.hashCode();
	}
	
	//-------- template methods --------
	
	/**
	 *  Remove local listeners.
	 */
	protected void	dispose()
	{
		if(timer!=null)
		{
//			System.out.println("cancel timer: "+this);
			timer.cancel();
			timer	= null;
		}
	}
}
