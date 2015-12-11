package jadex.extension.envsupport.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.IResultListener;

/**
 *  The list of scheduled component actions and convenience methods for
 *  executing selected actions.
 *  This implementation is not thread-safe, i.e. methods
 *  should only be called from threads that are already synchronized
 *  with the environment space monitor.
 */
public class ComponentActionList 
{
	//-------- attributes --------

	/** The environment space. */
	protected IEnvironmentSpace	space;

	/** The scheduled actions. */
	protected Set<ActionEntry>	actions;
	
	/** The executed actions where actors still need to be woken up. */
	protected Collection<ActionEntry>	executed;
	
	/** The schedule command. */
	protected ICommand	cmd;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action list.
	 */
	public ComponentActionList(IEnvironmentSpace space)
	{
		this.space	= space;
	}
	
	//-------- methods --------
	
	/**
	 * Schedules an component action.
	 * @param action	The action.
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public int scheduleComponentAction(ISpaceAction action, Map parameters, IResultListener listener)
	{
		ActionEntry	entry	= new ActionEntry(action, parameters, listener);
		
		// If command is set, invoke it.
		if(cmd!=null)
			cmd.execute(entry);

		// Otherwise queue action (default).
		else
			addComponentAction(entry);
		
		return entry.id;
	}
	
	/**
	 * Cancels a component action.
	 */
	public void cancelComponentAction(int id)
	{
		for(ActionEntry entry: actions)
		{
			if(entry.id==id)
			{
				entry.invalid	= true;
			}
		}
	}

	
	/**
	 * Add an component action.
	 * @param entry	The action entry.
	 */
	public void addComponentAction(ActionEntry entry)
	{
		if(actions==null)
			actions	= new LinkedHashSet<ActionEntry>();
		
		actions.add(entry);
	}

	/**
	 * Remove an component action.
	 * @param entry	The action entry.
	 */
	public void removeComponentAction(ActionEntry entry)
	{
		if(actions!=null)
		{		
			actions.remove(entry);
		}
	}

	/**
	 *  Get the queued entries, which have not yet been executed.
	 */
	public ActionEntry[]	getActionEntries()
	{
		ActionEntry[]	ret;
		if(actions!=null)
		{
			ret	= (ActionEntry[])actions.toArray(new ActionEntry[actions.size()]);
		}
		else
		{
			ret	= new ActionEntry[0];
		}
		return ret;
	}

	/**
	 *  Set an ordering used for executing actions.
	 *  @param comp	The comparator representing the ordering.
	 */
	public void	setOrdering(Comparator comp)
	{
		if(actions!=null)
		{
			Set<ActionEntry>	tmp	= new TreeSet<ActionEntry>(comp);
			tmp.addAll(actions);
			actions	= tmp;
		}
		else
		{
			actions	= new TreeSet<ActionEntry>(comp);
		}
	}
	
	/**
	 *  Should be called on environment thread only.
	 *  @param filter	A filter to select only a subset of actions (or null for all actions).
	 *  @param wakeup	Immediately wake up each calling component after its action has been executed
	 *  (otherwise wakeupComponents() has to be called separately).
	 */
	public void executeActions(IFilter filter, boolean wakeup)
	{
		if(actions!=null && !(actions.isEmpty()))
		{
			for(Iterator it=actions.iterator(); it.hasNext(); )
			{
				ActionEntry entry = (ActionEntry)it.next();
				try
				{
					if(filter==null || filter.filter(entry))
					{
						it.remove();
						try
						{
//							System.out.println("Action: "+entry);
							if(!entry.isInvalid())
							{
								Object ret = entry.action.perform(entry.parameters, space);
								if(entry.listener!=null)
								{
									if(wakeup)
									{
										entry.listener.resultAvailable(ret);
									}
									else
									{
										entry.result	= ret;
										if(executed==null)
											executed	= new ArrayList();
										executed.add(entry);
									}
								}
							}
							else
							{
								if(entry.listener!=null)
								{
									Exception e = new RuntimeException("Invalid action.");
									if(wakeup)
									{
										entry.listener.exceptionOccurred(e);
									}
									else
									{
										entry.exception	= e;
										if(executed==null)
											executed	= new ArrayList();
										executed.add(entry);
									}
								}
							}
						}
						catch(Exception e)
						{
							if(entry.listener!=null)
							{
								if(wakeup)
								{
									entry.listener.exceptionOccurred(e);
								}
								else
								{
									entry.exception	= e;
									if(executed==null)
										executed	= new ArrayList();
									executed.add(entry);
								}
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  Should be called on environment thread only.
	 *  @param filter	A filter to select only a subset of actions (or null for all actions).
	 *  (otherwise wakeupComponents() has to be called separately).
	 */
	public void wakeupComponents(IFilter filter)
	{
		if(executed!=null && !(executed.isEmpty()))
		{
			for(Iterator it=executed.iterator(); it.hasNext(); )
			{
				ActionEntry entry = (ActionEntry)it.next();
				try
				{
					if(filter==null || filter.filter(entry))
					{
						it.remove();
						if(entry.exception==null)
						{
							entry.listener.resultAvailable(entry.result);
						}
						else
						{
							entry.listener.exceptionOccurred(entry.exception);
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  Set the schedule command to be invoked, when an action should be scheduled.
	 *  Per default, an action is added to the list, but custom commands
	 *  might decide to execute action immediately or alter the list in arbitrary ways.
	 *  The command parameter is of type ActionEntry.
	 */
	public void	setScheduleCommand(ICommand cmd)
	{
		if(this.cmd!=null)
			throw new RuntimeException("Can set command only once");
		this.cmd	= cmd;
	}
	
	//-------- helper classes --------
	
	/**
	 *  Entry for a scheduled action.
	 */
	public static class ActionEntry	implements Comparable
	{
		//-------- static part --------
		
		protected static int CNT	= 0;
		
		//-------- attributes --------
		
		/** The action. */
		public ISpaceAction	action;
		
		/** The action parameters. */
		public Map	parameters;
		
		/** The result listener. */
		public IResultListener	listener;
		
		/** The result (set after successful execution). */
		public Object	result;
		
		/** The exception (set after failed execution). */
		public Exception	exception;
		
		/** An id to differentiate otherwise equal actions. */
		public int	id;
		
		/** Flag indicating that the action is invalid (e.g. when actor was destroyed in meantime). */
		private boolean invalid;
		
		//-------- constructors --------
		
		/**
		 *  Convenience constructor for inline entry creation.
		 */
		public ActionEntry(ISpaceAction action, Map parameters, IResultListener listener)
		{
			 this.action	= action;
			 this.parameters	= parameters;
			 this.listener	= listener;
			 synchronized(ActionEntry.class)
			 {
				 this.id	= CNT++;
			 }
		}
		
		//-------- Comparable interface --------
		
		/**
		 *  Compare two action entries.
		 */
		public int compareTo(Object obj)
		{
			return id - ((ActionEntry)obj).id;
		}
		
		//-------- methods --------
		
		/**
		 *  Get the invalid.
		 *  @return The invalid.
		 */
		public boolean isInvalid()
		{
			return invalid;
		}

		/**
		 *  Set the invalid.
		 *  @param invalid The invalid to set.
		 */
		public void setInvalid(boolean invalid)
		{
			this.invalid = invalid;
		}

		/**
		 *  Create a string representation of the action.
		 */
		public String	toString()
		{
			return ""+action+parameters;
		}
	}

	
//	/**
//	 * 
//	 */
//	public static class OwnerFilter implements IFilter
//	{
//		/** The owner. */
//		protected Object owner;
//	
//		/**
//		 *  Test if an object passes the filter.
//		 *  @return True, if passes the filter.
//		 */
//		public boolean filter(Object obj)
//		{
//			boolean ret = false;
//			Entry entry = (Entry)obj;
//			
//			if(entry.getMetainfo() instanceof DefaultEntryMetaInfo)
//			{
//				DefaultEntryMetaInfo mi = (DefaultEntryMetaInfo)entry.getMetainfo();
//				ret = SUtil.equals(owner, mi.getOwner());
//			}
//			
//			return ret;
//		}
//	}
}
