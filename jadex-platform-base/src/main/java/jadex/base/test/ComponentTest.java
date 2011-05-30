package jadex.base.test;


import jadex.bridge.IComponentManagementService;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 *  Test a component.
 */
public class ComponentTest implements	Test
{
	//-------- attributes --------
	
	/** The component management system. */
	protected IComponentManagementService	cms;
	
	/** The component. */
	protected String	comp;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public ComponentTest(IComponentManagementService cms, String comp)
	{
		this.cms	= cms;
		this.comp	= comp;
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
		
		// Start the component.
//		System.out.println("Starting test: "+comp);
		TestResultListener	trl	= new TestResultListener();
//		Map	args	= new HashMap();
//		args.put("timeout", new Long(3000000));
//		CreationInfo	ci	= new CreationInfo(args);
		cms.createComponent(null, comp, null, trl).get(new ThreadSuspendable(), 300000);

		// Evaluate the results.
		try
		{
			Testcase	tc	= trl.waitForResult();
			TestReport[]	reports	= tc.getReports();
			if(tc.getTestCount()!=reports.length)
			{
				result.addFailure(this, new AssertionFailedError("Number of testcases do not match. Expected "+tc.getTestCount()+" but was "+reports.length+"."));			
			}
			for(int i=0; i<reports.length; i++)
			{
				if(!reports[i].isSucceeded())
				{
					result.addFailure(this, new AssertionFailedError(reports[i].getDescription()+" Failed with reason: "+reports[i].getReason()));
				}
			}
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
		return comp;
	}
	
	//-------- helper classes --------

	/**
	 *  Listener to capture component execution results.
	 */
	class TestResultListener implements IResultListener
	{
		//-------- attributes --------
		
		/** Flag to check if execution is finished. */
		protected boolean	finished;
		
		/** The result or exception of the execution. */
		protected Object	result;

		//-------- IResultListener interface --------
		
		/**
		 *  Called when an exception occurred during component execution.
		 */
		public void exceptionOccurred(Exception exception)
		{
			synchronized(this)
			{
//				System.out.println("Test execution error: "+comp);
				result	= exception;
				finished	= true;
				notify();
			}
		}
		
		/**
		 *  Called when the component has terminated.
		 */
		public void resultAvailable(Object res)
		{
			synchronized(this)
			{
//				System.out.println("Test finished: "+comp);
				result	= res;
				finished	= true;
				notify();
			}
		}
		
		//-------- methods --------
		
		/**
		 *  Wait for component execution to finish and return the result.
		 */
		public Testcase	waitForResult() throws Exception
		{
			Testcase	ret;
			
			// Wait for the component execution to finish.
			synchronized(this)
			{
				if(!finished)
				{
					wait();
				}
			}
			
			// Fetch the result.
			if(result instanceof Exception)
			{
				throw (Exception)result;
			}
			else
			{
				ret	= (Testcase)((Map)result).get("testresults");
			}

			return ret;
		}
	}
}
