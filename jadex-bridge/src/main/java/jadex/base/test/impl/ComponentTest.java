package jadex.base.test.impl;


import jadex.base.test.ComponentTestSuite;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 *  Test a component.
 */
public class ComponentTest extends TestCase
{
	//-------- attributes --------
	
	/** The component management system. */
	protected IComponentManagementService	cms;
	
	/** The component. */
	protected IModelInfo	comp;
	
	/** The test suite. */
	protected ComponentTestSuite	suite;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public ComponentTest(IComponentManagementService cms, IModelInfo comp, ComponentTestSuite suite)
	{
		this.cms	= cms;
		this.comp	= comp;
		this.suite	= suite;
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
		if(suite.isAborted())
		{
			return;
		}
		
		result.startTest(this);
		
		// Start the component.
//		System.out.println("Starting test: "+comp);
		TestResultListener	trl	= new TestResultListener();
//		Map	args	= new HashMap();
//		args.put("timeout", new Long(3000000));
//		CreationInfo	ci	= new CreationInfo(args);

		// Evaluate the results.
		try
		{
			cms.createComponent(null, comp.getFilename(), new CreationInfo(comp.getResourceIdentifier()), trl).get(new ThreadSuspendable(), SReflect.isAndroid() ? 600000 : BasicService.getLocalDefaultTimeout());
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

		// Remove references to Jadex resources to aid GC cleanup.
		cms	= null;
		comp	= null;
		suite	= null;
	}
	
	public String getName()
	{
		return this.toString();
	}
	
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return comp.getFullName();
	}
	
	//-------- helper classes --------

	/**
	 *  Listener to capture component execution results.
	 */
//	class TestResultListener implements IResultListener<Map<String, Object>>
	class TestResultListener implements IResultListener<Collection<Tuple2<String, Object>>>
	{
		//-------- attributes --------
		
		/** Flag to check if execution is finished. */
		protected boolean	finished;
		
		/** The result of the execution (if not exception). */
		protected Collection<Tuple2<String, Object>>	result;
		
		/** The exception of the execution (if any). */
		protected Exception	exception;

		//-------- IResultListener interface --------
		
		/**
		 *  Called when an exception occurred during component execution.
		 */
		public void exceptionOccurred(Exception exception)
		{
			synchronized(this)
			{
//				System.out.println("Test execution error: "+comp);
				this.exception	= exception;
				finished	= true;
				notify();
			}
		}
		
		/**
		 *  Called when the component has terminated.
		 */
		public void resultAvailable(Collection<Tuple2<String, Object>> res)
		{
			synchronized(this)
			{
//				System.out.println("Test finished: "+comp);
				this.result	= res;
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
			Testcase	ret = null;
			
			// Wait for the component execution to finish.
			synchronized(this)
			{
				if(!finished)
				{
					wait();
				}
			}
			
			// Fetch the result.
			if(exception!=null)
			{
				throw exception;
			}
			else
			{
				for(Iterator<Tuple2<String, Object>> it=result.iterator(); it.hasNext(); )
				{
					Tuple2<String, Object> tup = it.next();
					if(tup.getFirstEntity().equals("testresults"))
					{
						ret = (Testcase)tup.getSecondEntity();
						break;
					}
				}
//				ret	= (Testcase)result.get("testresults");
			}

			return ret;
		}
	}
}
