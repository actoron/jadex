package jadex.base.test.impl;


import java.util.TimerTask;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.ThreadSuspendable;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 *  This test kills the platform.
 *  Used as last test in the component test suite for cleanup.
 */
public class Cleanup implements	Test
{
	//-------- attributes --------
	
	/** The platform access. */
	protected IExternalAccess	platform;
	
	/** The timer that stops test suite execution after timeout (needs to be cancelled on cleanup). */
	protected	TimerTask	timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public Cleanup(IExternalAccess platform, TimerTask timer)
	{
		this.platform	= platform;
		this.timer	= timer;
	}
	
	//-------- methods --------
	
	/**
	 *  The number of test cases.
	 */
	public int countTestCases()
	{
		return 1;
	}
	
	/**
	 *  Test the component.
	 */
	public void run(TestResult result)
	{
		timer.cancel();
		
		result.startTest(this);

		try
		{
			platform.killComponent().get(new ThreadSuspendable(), 30000);
		}
		catch(Exception e)
		{
			result.addError(this, e);
		}
		
//		try
//		{
//			Thread.sleep(300000);
//		}
//		catch(InterruptedException e)
//		{
//		}
		
		result.endTest(this);
	}
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return "Cleanup";
	}
}
