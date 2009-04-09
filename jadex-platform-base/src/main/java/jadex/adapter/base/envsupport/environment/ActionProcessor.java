package jadex.adapter.base.envsupport.environment;

import jadex.commons.IFilter;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 */
public class ActionProcessor 
{
	public static final Comparator DEFAULT_COMPARATOR = new DefaultComparator();
	
	//-------- attributes --------
	
	/** Global environment monitor */
	protected transient Object monitor;
	
	/** The entries added from external threads. */
	protected transient final Set ext_entries;
	
	/** The thread executing the active object (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread mythread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public ActionProcessor(Object monitor)
	{
		this(monitor, null);
	}
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public ActionProcessor(Object monitor, Comparator comp)
	{
		this.monitor = monitor;
		this.ext_entries = Collections.synchronizedSet(new TreeSet(comp==null? DEFAULT_COMPARATOR: comp));
	}
	
	/**
	 *  Can be called on own thread only.
	 */
	public void executeEntries(IFilter filter)
	{
		this.mythread = Thread.currentThread();
		
		List todo = null;
		
		synchronized(monitor)
		{
			if(!(ext_entries.isEmpty()))
			{
				todo = new ArrayList();
				for(Iterator it=ext_entries.iterator(); it.hasNext(); )
				{
					Object tmp = it.next();
					if(filter==null || filter.filter(tmp))
					{
						todo.add(tmp);
						it.remove();
					}
				}
			}
		}
		
		for(int i=0; todo!=null && i<todo.size(); i++)
		{
			try
			{
				((Entry)todo.get(i)).getRunnable().run();
			}
			catch(Exception e)
			{
				System.err.println("Execution led to exeception: "+e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Get the entries.
	 *  @return The entries.
	 * /
	public Entry[] getEntries()
	{
		synchronized(monitor)
		{
			return (Entry[])ext_entries.toArray(new Entry[ext_entries.size()]);
		}
	}*/
	
	/**
	 *  Remove an entry.
	 * /
	public void removeEntry(Runnable entry)
	{
		synchronized(monitor)
		{
			ext_entries.remove(entry);
		}
	}*/

	//-------- helpers --------
	
	/**
	 *  Add an action from external thread.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action)
	{
		invokeLater(action, new DefaultEntryMetaInfo(0, null));
	}
	
	/**
	 *  Add an action from external thread.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action, Object metainfo)
	{
		synchronized(monitor)
		{
			ext_entries.add(new Entry(action, metainfo));
		}
//		adapter.wakeup();
	}
	
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 */
	public void invokeSynchronized(final Runnable code)
	{
		invokeSynchronized(code, new DefaultEntryMetaInfo(0, null));
	}
	
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 */
	public void invokeSynchronized(final Runnable code, Object metainfo)
	{
		if(isExternalThread())
		{
//			System.err.println("Unsynchronized internal thread.");
//			Thread.dumpStack();

			final boolean[] notified = new boolean[1];
			final RuntimeException[] exception = new RuntimeException[1];
			
			// Add external will throw exception if action execution cannot be done.
//			System.err.println("invokeSynchonized("+code+"): adding");
			invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						code.run();
					}
					catch(RuntimeException e)
					{
						exception[0]	= e;
					}
					
					synchronized(notified)
					{
						notified.notify();
						notified[0] = true;
					}
				}
				
				public String	toString()
				{
					return code.toString();
				}
			}, metainfo);
			
			try
			{
//				System.err.println("invokeSynchonized("+code+"): waiting");
				synchronized(notified)
				{
					if(!notified[0])
					{
						notified.wait();
					}
				}
//				System.err.println("invokeSynchonized("+code+"): returned");
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			if(exception[0]!=null)
				throw exception[0];
		}
		else
		{
			System.err.println("Method called from own thread.");
			Thread.dumpStack();
			code.run();
		}
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isExternalThread()
	{
		return !(mythread==Thread.currentThread());
	}
	
	/**
	 *  Get the monitor used for synchronization.
	 */
	public Object getMonitor()
	{
		return ext_entries;
	}
	
	/**
	 *  Set the monitor used for synchronization.
	 */
	public void setMonitor(Object monitor)
	{
		this.monitor = monitor;
	}
	
	/**
	 * 
	 */
	public static class Entry
	{
		/** The runnable. */
		protected Runnable runnable;
		
		/** The metainfo. */
		protected Object metainfo;

		/**
		 *  Create a new entry.
		 *  @param runnable
		 *  @param metainfo
		 */
		public Entry(Runnable runnable, Object metainfo)
		{
			this.runnable = runnable;
			this.metainfo = metainfo;
		}

		/**
		 * @return the runnable
		 */
		public Runnable getRunnable()
		{
			return this.runnable;
		}

		/**
		 * @return the metainfo
		 */
		public Object getMetainfo()
		{
			return this.metainfo;
		}
	}
	
	/**
	 * 
	 */
	public static class DefaultEntryMetaInfo
	{
		/** The static counter. */
		protected static int counter;
		
		/** The priority. */
		protected int priority;

		/** The count. */
		protected int count;
		
		/** The owner. */
		protected Object owner;

		/**
		 * 
		 * @param priority
		 * @param owner
		 */
		public DefaultEntryMetaInfo(int priority, Object owner)
		{
			this.priority = priority;
			this.owner = owner;
			synchronized(DefaultEntryMetaInfo.class)
			{
				this.count = counter++;
			}
		}

		/**
		 * @return the priority
		 */
		public int getPriority()
		{
			return this.priority;
		}

		/**
		 * @return the owner
		 */
		public Object getOwner()
		{
			return this.owner;
		}

		/**
		 * @return the count
		 */
		public int getCount()
		{
			return this.count;
		}
	}
	
	/**
	 *  Compare runnable entries.
	 */
	static final class DefaultComparator implements Comparator
	{
		public int compare(Object arg0, Object arg1)
		{
			int ret;
			
			Entry e0 = (Entry)arg0;
			Entry e1 = (Entry)arg1;
			
			if(e0.getMetainfo() instanceof DefaultEntryMetaInfo
				&& e1.getMetainfo() instanceof DefaultEntryMetaInfo)
			{
				DefaultEntryMetaInfo m1 = (DefaultEntryMetaInfo)e0.getMetainfo();
				DefaultEntryMetaInfo m2 = (DefaultEntryMetaInfo)e1.getMetainfo();
				ret = m1.getPriority() - m2.getPriority();
				if(ret==0)
					ret = m1.getCount() - m2.getCount();
			}
			else
			{
				throw new RuntimeException("Unknown entry meta info");
			}

			return ret;
		}
	}
	
	/**
	 * 
	 */
	public static class OwnerFilter implements IFilter
	{
		/** The owner. */
		protected Object owner;
	
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(Object obj)
		{
			boolean ret = false;
			Entry entry = (Entry)obj;
			
			if(entry.getMetainfo() instanceof DefaultEntryMetaInfo)
			{
				DefaultEntryMetaInfo mi = (DefaultEntryMetaInfo)entry.getMetainfo();
				ret = SUtil.equals(owner, mi.getOwner());
			}
			
			return ret;
		}
	}
}
