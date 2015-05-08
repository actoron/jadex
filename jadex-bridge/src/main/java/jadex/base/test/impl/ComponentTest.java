package jadex.base.test.impl;


import jadex.base.test.ComponentTestSuite;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.future.TupleResult;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 *  Test a component.
 */
public class ComponentTest extends TestCase
{
	//-------- attributes --------
	
	/** The component management system. */
	protected IComponentManagementService	cms;
	
	/** The component model. */
	protected String	filename;
	
	/** The component resource identifier. */
	protected IResourceIdentifier	rid;
	
	/** The component full name. */
	protected String	fullname;
	
	/** The timeout. */
	protected long	timeout;
	
	/** The test suite. */
	protected ComponentTestSuite	suite;
	
	//-------- constructors --------
	
	public ComponentTest() {
		Logger.getLogger("ComponentTest").log(Level.SEVERE, "Empty ComponentTest Constructor called");
	}
	
	/**
	 *  Create a component test.
	 */
	public ComponentTest(IComponentManagementService cms, IModelInfo comp, ComponentTestSuite suite)
	{
		this.cms	= cms;
		this.filename	= comp.getFilename();
		this.rid	= comp.getResourceIdentifier();
		this.fullname	= comp.getFullName();
		Object	to	= comp.getProperty(Testcase.PROPERTY_TEST_TIMEOUT, suite.getClassLoader());
		if(to!=null)
		{
			this.timeout	= ((Number)to).longValue();
		}
		else
		{
			this.timeout	= BasicService.getLocalDefaultTimeout();
		}
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
	public void runBare()
	{
		
		if(suite.isAborted())
		{
			return;
		}
		
//		System.out.println("starting: "+filename);
		
		// Start the component.
		ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
		final Future<Map<String, Object>>	finished	= new Future<Map<String,Object>>();
		Timer	t	= new Timer(true);
		if(timeout>0)
		{
			t.schedule(new TimerTask()
			{
				public void run()
				{
					finished.setExceptionIfUndone(new TimeoutException(ComponentTest.this+" did not finish in "+timeout+" ms."));
				}
			}, timeout);
		}

		final ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut	= cms.createComponent(null, filename, new CreationInfo(rid));
		fut.addResultListener(new ExceptionDelegationResultListener<Collection<TupleResult>, Map<String, Object>>(finished)
		{
			public void customResultAvailable(Collection<TupleResult> result)
			{
				finished.setResult(fut.getSecondResult());
			}
		});

		// Evaluate the results.
		Map<String, Object>	res	= finished.get();
		t.cancel();
//		System.out.println("finished: "+filename);
		Testcase	tc	= null;
		for(Iterator<Map.Entry<String, Object>> it=res.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Object> tup = it.next();
			if(tup.getKey().equals("testresults"))
			{
				tc = (Testcase)tup.getValue();
				break;
			}
		}
		
		if(tc!=null && tc.getReports()!=null)
		{
			TestReport[]	reports	= tc.getReports();
			if(tc.getTestCount()!=reports.length)
			{
				throw new AssertionFailedError("Number of testcases do not match. Expected "+tc.getTestCount()+" but was "+reports.length+".");			
			}
			for(int i=0; i<reports.length; i++)
			{
				if(!reports[i].isSucceeded())
				{
					throw new AssertionFailedError(reports[i].getDescription()+" Failed with reason: "+reports[i].getReason());
				}
			}
		}
		else
		{
			throw new AssertionFailedError("No test results provided by component: "+res);
		}

		// Remove references to Jadex resources to aid GC cleanup.
		cms	= null;
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
		return fullname;
	}
}
