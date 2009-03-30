package jadex.adapter.base.envsupport.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class SynchronizedObject //implements ISynchronizator
{
	//-------- attributes --------
	
	/** The entries added from external threads. */
	protected transient final List ext_entries;
	
	/** The thread executing the active object (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread mythread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public SynchronizedObject()
	{
		this.ext_entries = Collections.synchronizedList(new ArrayList());
	}
	
	/**
	 *  Can be called on own thread only.
	 */
	public void executeEntries()
	{
		this.mythread = Thread.currentThread();
		
		Runnable[]	entries	= null;
		
		synchronized(ext_entries)
		{
			if(!(ext_entries.isEmpty()))
			{
				entries	= (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
				ext_entries.clear();
			}
		}
		for(int i=0; entries!=null && i<entries.length; i++)
		{
			try
			{
				entries[i].run();
			}
			catch(Exception e)
			{
				System.err.println("Execution led to exeception: "+e);
			}
		}
	}
	
	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public Runnable[] getEntries()
	{
		synchronized(ext_entries)
		{
			return (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
		}
	}
	
	/**
	 *  Remove an entry.
	 */
	public void removeEntry(Runnable entry)
	{
		synchronized(ext_entries)
		{
			ext_entries.remove(entry);
		}
	}

	//-------- helpers --------
	
	/**
	 *  Add an action from external thread.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action)
	{
		synchronized(ext_entries)
		{
			ext_entries.add(action);
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
			});
			
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
}
