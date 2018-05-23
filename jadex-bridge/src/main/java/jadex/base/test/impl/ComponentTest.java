package jadex.base.test.impl;


import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.TupleResult;
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
	
	/** The platform configuration */
	protected IPlatformConfiguration	conf;
	
	/** Additional config args. */
	protected String[]	args;
	
	/** The dirs for rids (e.g. classes and resources dirs). */
	protected File[][]	dirs;
	
	/** The component model. */
	protected String	filename;
	
	/** The component resource identifier. */
	protected IResourceIdentifier	rid;
	
	/** The component full name. */
	protected String	fullname;
	
	/** The component (kernel) type. */
	protected String	type;
	
	/** The timeout. */
	protected long	timeout;
	
	/** The test suite. */
	protected IAbortableTestSuite suite;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ComponentTest.
	 */
	public ComponentTest() 
	{
		Logger.getLogger("ComponentTest").log(Level.SEVERE, "Empty ComponentTest Constructor called");
	}
	
	/**
	 *  Create a component test.
	 *  Run on existing test suite platform.
	 *  @param cms	The CMS of the test suite platform.
	 */
	public ComponentTest(IComponentManagementService cms, IModelInfo comp, IAbortableTestSuite suite)
	{
		this.cms	= cms;
		this.filename	= comp.getFilename();
		this.rid	= comp.getResourceIdentifier();
		this.fullname	= comp.getFullName();
		this.type	= comp.getType();
		Object	to	= comp.getProperty(Testcase.PROPERTY_TEST_TIMEOUT, getClass().getClassLoader());
		if(to!=null)
		{
			this.timeout	= ((Number)to).longValue();
		}
		else
		{
			this.timeout	= Starter.getLocalDefaultTimeout(((IService)cms).getServiceIdentifier().getProviderId());
		}
		this.suite	= suite;		
	}
	
	/**
	 *  Create a component test.
	 *  Run on separate platform.
	 *  @param conf	The config for the new platform.
	 */
	public ComponentTest(IPlatformConfiguration conf, String[] args,  File[][] dirs, IComponentManagementService cms, IModelInfo comp, IAbortableTestSuite suite)
	{
		this.conf	= conf;
		this.args	= args;
		this.dirs	= dirs;
		//	this.cms	= cms; // Don't store suite cms -> use for own cms later.
		this.filename	= comp.getFilename();
		this.rid	= comp.getResourceIdentifier();
		this.fullname	= comp.getFullName();
		this.type	= comp.getType();
		Object	to	= comp.getProperty(Testcase.PROPERTY_TEST_TIMEOUT, getClass().getClassLoader());
		if(to!=null)
		{
			this.timeout	= ((Number)to).longValue();
		}
		else
		{
			this.timeout	= Starter.getLocalDefaultTimeout(((IService)cms).getServiceIdentifier().getProviderId());
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
		// Hack for testing.
		timeout=timeout << 2;
		if(suite!=null && suite.isAborted())
		{
			return;
		}
		
		// Start the component.
		final IComponentIdentifier[]	cid	= new IComponentIdentifier[1];
		final Future<Map<String, Object>>	finished	= new Future<Map<String,Object>>();
		Timer	t	= null;
		final boolean[]	triggered	= new boolean[1];	
		
		if(timeout!=Timeout.NONE)
		{
			t	= new Timer(true);
			
//			System.out.println("Using test timeout: "+timeout+" "+System.currentTimeMillis()+" "+filename);
			
			t.schedule(new TimerTask()
			{
				public void run()
				{
//					System.out.println("TIMEOUT: "+System.currentTimeMillis()+" "+filename);

					triggered[0] = true;
					boolean	b = finished.setExceptionIfUndone(new TimeoutException(ComponentTest.this+" did not finish in "+timeout+" ms."));
					IComponentManagementService	cms	= ComponentTest.this.cms;
					if(b && cid[0]!=null && cms!=null)
					{
						cms.destroyComponent(cid[0]);
					}
				}
			}, timeout);
		}

		// Actually not needed, because create component has no timoeut (hack???)
		 ServiceCall.getOrCreateNextInvocation().setTimeout(timeout);
		
		if(conf!=null)
		{
			IExternalAccess	exta	= Starter.createPlatform(conf, args).get(timeout, true);
			cms	= SServiceProvider.getService(exta, IComponentManagementService.class).get(timeout, true);
			ILibraryService	libsrv	= SServiceProvider.getService(exta, ILibraryService.class).get(timeout, true);
			
			for (int projectIndex=0; projectIndex < dirs.length; projectIndex++) {
				File[] project = dirs[projectIndex];
				IResourceIdentifier	parentRid	= null;
				for(int rootIndex=0; rootIndex<project.length; rootIndex++)
				{
					try
					{
						if(parentRid==null && rid.getLocalIdentifier().getUri().equals(project[rootIndex].getCanonicalFile().toURI()))
						{
							System.out.println(fullname+": choose "+project[rootIndex]+" as "+rid);
							parentRid	= rid;
							libsrv.addURL(null, project[rootIndex].toURI().toURL()).get(timeout, true);
						}
						else if(parentRid!=null)
						{
							System.out.println(fullname+": add "+project[rootIndex]+" to "+rid);
							libsrv.addURL(parentRid, project[rootIndex].toURI().toURL()).get(timeout, true);
						}
						else
						{
							System.out.println(fullname+": no match "+project[rootIndex]+" for "+rid);
						}
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		 
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut	= cms.createComponent(null, filename, new CreationInfo(rid));
		componentStarted(fut);
		fut.addResultListener(new IntermediateDefaultResultListener<TupleResult>()
		{
			@SuppressWarnings("unchecked")
			public void intermediateResultAvailable(TupleResult result)
			{
				if(result.getNum()==0)
				{
					cid[0]	= (IComponentIdentifier)result.getResult();
				}
				else
				{
//					if(filename.toString().indexOf("Feature")!=-1)
//						Thread.dumpStack();
					finished.setResultIfUndone((Map<String, Object>)result.getResult());
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				finished.setExceptionIfUndone(exception);
			}
		});
		Map<String, Object>	res	= null;
		try
		{
			res	= finished.get();	// Timeout set by timer above -> no get timeout needed.
		}
		catch(TimeoutException te)
		{
			te.printStackTrace();
			// Hack!! Allow timeout exception for start tests when not from test execution, e.g. termination timeout in EndStateAbort.
			if(triggered[0])
			{
				throw te;
			}
		}
		if(t!=null)
		{
			t.cancel();
		}
		
		// cleanup platform?
		if(conf!=null)
		{
			cms.destroyComponent(cms.getRootIdentifier().get(timeout, true)).get(timeout, true);
		}
		
		// Remove references to Jadex resources to aid GC cleanup.
		cms	= null;
		suite	= null;
		
		checkTestResults(res);	// Do last -> throws exception on failure.
	}

	/**
	 *  Called when a component has been started.
	 *  @param cid	The cid, set as soon as known.
	 */
	protected void componentStarted(ITuple2Future<IComponentIdentifier, Map<String, Object>> fut)
	{
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

	public String getName()
	{
		return this.toString();
	}
	
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return fullname + " (" + type + ")";
	}

	/**
	 *  Get the timeout.
	 */
	public long	getTimeout()
	{
		return timeout;
	}
}
