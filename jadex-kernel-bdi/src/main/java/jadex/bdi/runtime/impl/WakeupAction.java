package jadex.bdi.runtime.impl;

import jadex.bridge.CheckedAction;

/**
 *  The action class for continuing the external thread.
 */
public class WakeupAction extends CheckedAction
{
	//-------- attributes --------
	
	/** The caller thread. */
	protected Thread callerthread;
	
	/** The timeout flag. */
	protected boolean timeout;
	
	//-------- constructors --------
	
	/**
	 *  Create a new wakeup action.
	 */
	public WakeupAction(Thread callerthread)
	{
		this.callerthread = callerthread;
		this.timeout	= true;
	}
	
	//-------- methods --------
	
	/**
	 *  The code to continue an external thread.
	 */
	public void run()
	{
//		System.out.println("run: "+this);
		// Requires asynchronous call,otherwise deadlock occurs,
		// when external access tries to do more actions on agent thread before waiting
		// (e.g. for sendMessage-/dispatchGoal- AndWait)
		// or when external access is woken up twice (e.g. event + timer)
		// Todo: Only required for -AndWait cases???
		new Thread(new Runnable()
		{
			public void run()
			{
				synchronized(callerthread)
				{
					if(callerthread.isAlive())
						callerthread.notify();
				}
			}
		}).start();
	}
	
	/**
	 *  Get the timeout flag.
	 *  @return True when a timeout occurred.
	 */
	public boolean isTimeout()
	{
		return timeout;
	}
	
	/**
	 *  Set the timeout flag to false when an event was dispatched.
	 */
	public void setTimeout(boolean timeout)
	{
		this.timeout	= timeout;
	}
}
