package jadex.base.test.impl;


import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 *  Test a component.
 */
public class ComponentTestBase extends TestCase
{
	//-------- attributes --------
	
	/** The component management system. */
	protected IExternalAccess platform;
	
	/** The platform configuration */
	protected IPlatformConfiguration	conf;
	
	/** The component model. */
	protected String	filename;
	
	/** The component resource identifier. */
	protected IResourceIdentifier	rid;
	
	/** The timeout. */
	protected long	timeout;
	
	/** The test suite. */
	protected IAbortableTestSuite suite;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ComponentTest.
	 */
	public ComponentTestBase() 
	{
		//Logger.getLogger("ComponentTest").log(Level.SEVERE, "Empty ComponentTest Constructor called");
	}
	
	/**
	 *  Create a component test.
	 *  Run on existing test suite platform.
	 *  @param cms	The CMS of the test suite platform.
	 */
	public ComponentTestBase(String comp, IAbortableTestSuite suite)
	{
		super(comp);
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
		Timer t = null;
		try
		{
			if(suite!=null && suite.isAborted())
				return;
			
			// Start the component.
			final IComponentIdentifier[]	cid	= new IComponentIdentifier[1];
			final Future<Map<String, Object>> finished = new Future<Map<String,Object>>();
			final boolean[]	triggered	= new boolean[1];	
			
			if(timeout!=Timeout.NONE)
			{
				t = new Timer(true);
				
				System.out.println("Using test timeout: "+timeout+" "+System.currentTimeMillis()+" "+filename);
				
				t.schedule(new TimerTask()
				{
					public void run()
					{
						triggered[0] = true;
						boolean	b = finished.setExceptionIfUndone(new TimeoutException(ComponentTestBase.this+" did not finish in "+timeout+" ms."));
						if(b)
							System.out.println("TIMEOUT: "+System.currentTimeMillis()+" "+filename);
						if(b && cid[0]!=null && platform!=null)
						{
							System.out.println("KILLING: "+System.currentTimeMillis()+" "+filename);
							platform.getExternalAccess(cid[0]).killComponent();
							System.out.println("KILLED: "+System.currentTimeMillis()+" "+filename);
						}
					}
				}, timeout);
			}
	
			// Actually not needed, because create component has no timoeut (hack???)
			ServiceCall.getOrCreateNextInvocation().setTimeout(timeout);
			
			if(conf!=null)
			{
				platform = createPlatform();
			}
			 
			//System.out.println("Creating component: "+System.currentTimeMillis()+" "+filename);
			IFuture<IExternalAccess> fut = platform.createComponent(new CreationInfo(rid).setFilename(filename));
			componentStarted(fut);
			fut.addResultListener(new IResultListener<IExternalAccess>()
			{
				public void resultAvailable(IExternalAccess result)
				{
					//System.out.println("Created component: "+System.currentTimeMillis()+" "+filename);
					cid[0] = result.getId();
					
					result.waitForTermination().addResultListener(new IResultListener<Map<String,Object>>()
					{
						public void resultAvailable(Map<String, Object> result)
						{
							//System.out.println("COMP FINI: "+cid[0]);
							finished.setResultIfUndone(result);
						}
						public void exceptionOccurred(Exception exception)
						{
							//System.out.println("COMP FINI EX: "+cid[0]);
							finished.setExceptionIfUndone(exception);
						}
					});
				}
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("COMP EX: "+exception);
					finished.setExceptionIfUndone(exception);
				}
			});
			Map<String, Object>	res	= null;
//			try
//			{
				//System.out.println("WAIT FOR TESTCASE: "+cid[0]);
				res	= finished.get();	// Timeout set by timer above -> no get timeout needed.
				//System.out.println("TESTCASE FINISHED: "+cid[0]);
//			}
//			catch(TimeoutException te)
//			{
//				//System.out.println("TESTCASE TIMEOUT: "+cid[0]);
//				// Hack!! Allow timeout exception for start tests when not from test execution, e.g. termination timeout in EndStateAbort.
//				if(triggered[0])
//				{
//					throw te;
//				}
//			}
		
			checkTestResults(res);	// Do last -> throws exception on failure.
			//System.out.println("FINISHED runBare(): "+System.currentTimeMillis()+" "+filename);
		}
		catch(Throwable t2)
		{
			//System.out.println("FAILED runBare(): "+System.currentTimeMillis()+" "+filename);
			t2.printStackTrace();
			throw SUtil.throwUnchecked(t2);
		}
		finally
		{			
			// Remove references to Jadex resources to aid GC cleanup.
			suite = null;
			if(t!=null)
			{
				t.cancel();
			}

			// cleanup platform?
			if(conf!=null)
			{
				try
				{
					System.out.println("KILLING PLATFORM: "+System.currentTimeMillis()+" "+filename);
					platform.killComponent().get(timeout, true);
					System.out.println("KILLED PLATFORM: "+System.currentTimeMillis()+" "+filename);
				}
				catch(Throwable t3)
				{
					System.out.println("FAILED KILLING PLATFORM: "+System.currentTimeMillis()+" "+filename);
					t3.printStackTrace();
				}
				finally
				{
					platform	= null;
				}
			}
		}
	}

	/**
	 *  Called when a component has been started.
	 *  @param cid	The cid, set as soon as known.
	 */
	protected void componentStarted(IFuture<IExternalAccess> fut)
	{
	}

	/**
	 *  Create a new platform (only if conf is given and thus platform per test is enabled)
	 */
	protected IExternalAccess	createPlatform()
	{
		return null;
	}
	
	/**
	 *  Optional checking after component has finished.
	 *  @param res	The results.
	 */
	protected void checkTestResults(Map<String, Object> res)
	{
		// Evaluate the results.
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
	}
	
	/**
	 *  Get the timeout.
	 */
	public long	getTimeout()
	{
		return timeout;
	}
}
