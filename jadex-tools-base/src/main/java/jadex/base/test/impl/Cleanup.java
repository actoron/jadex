package jadex.base.test.impl;


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
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public Cleanup(IExternalAccess platform)
	{
		this.platform	= platform;
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
		result.startTest(this);

		try
		{
			platform.killComponent().get(new ThreadSuspendable(), 300000);
		}
		catch(Exception e)
		{
			result.addError(this, e);
		}
		
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
