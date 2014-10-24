package jadex.base.test.impl;


import jadex.base.Starter;
import jadex.base.test.ComponentTestSuite;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.Collection;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 *  Test if a component can be started.
 */
public class ComponentStartTest extends	TestCase
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
	
	/** The test suite. */
	protected ComponentTestSuite	suite;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public ComponentStartTest(IComponentManagementService cms, IModelInfo comp, ComponentTestSuite suite)
	{
		this.cms	= cms;
		this.filename	= comp.getFilename();
		this.rid	= comp.getResourceIdentifier();
		this.fullname	= comp.getFullName();
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
		
//		try
//		{
//			result.startTest(this);
//		}
//		catch(IllegalStateException e)
//		{
			// Hack: Android test runner tries to do getClass().getMethod(...) for test name, grrr.
			// See: http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.2.1_r1/android/test/InstrumentationTestRunner.java#767
//		}
		
		// Start the component.
		ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
//			System.out.println("starting: "+comp.getFilename());
		Future<Collection<Tuple2<String,Object>>>	finished	= new Future<Collection<Tuple2<String,Object>>>();
		final IComponentIdentifier	cid	= cms.createComponent(null, filename, new CreationInfo(rid), 
			new DelegationResultListener<Collection<Tuple2<String,Object>>>(finished)).get();
		try
		{
//				if(comp.getFilename().indexOf("Heatbugs")!=-1)
//				{
//					System.out.println("killing: "+comp.getFilename());
//					SyncExecutionService.DEBUG	= true;
//				}
			
			// Wait some time (simulation and real time) and kill the component afterwards.
			final IResultListener<Void>	lis	= new CounterResultListener<Void>(2, new DefaultResultListener<Void>()
			{
				public synchronized void resultAvailable(Void result)
				{
					IComponentManagementService	mycms	= cms;
					if(mycms!=null)
					{
						mycms.destroyComponent(cid);
					}
				}
			});
			IExternalAccess	ea	= cms.getExternalAccess(cid.getRoot()).get();
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					return ia.waitForDelay(500, false);
				}
			}).addResultListener(lis);
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					return ia.waitForDelay(500, true);
				}
			}).addResultListener(lis);
			
			finished.get(BasicService.getLocalDefaultTimeout());
//				System.out.println("killed: "+comp.getFilename());
		}
		catch(ComponentTerminatedException cte)
		{				
			// Ignore if component already terminated.
		}
		catch(RuntimeException e)
		{
			// Ignore if component already terminated.
			if(!(e.getCause() instanceof ComponentTerminatedException))
			{
				throw e;
			}
		}
		ISuspendable.SUSPENDABLE.set(null);
		
		// Remove references to Jadex resources to aid GC cleanup.
		cms	= null;
		suite	= null;
	}
	
	/**
	 *  Test the component.
	 */
	public static void dorun(IComponentManagementService cms, String filename)
	{
		// Start the component.
		try
		{
			System.out.println("starting: "+filename);
			IComponentIdentifier	cid	= cms.createComponent(null, filename, null, null).get(new ThreadSuspendable());
			try
			{
//				if(comp.getFilename().indexOf("Heatbugs")!=-1)
//				{
					System.out.println("killing: "+filename);
//					SyncExecutionService.DEBUG	= true;
//				}
				cms.destroyComponent(cid).get(new ThreadSuspendable());
				System.out.println("killed: "+filename);
			}
			catch(ComponentTerminatedException cte)
			{				
				// Ignore if component already terminated.
			}
			catch(RuntimeException e)
			{
				// Ignore if component already terminated.
				if(!(e.getCause() instanceof ComponentTerminatedException))
				{
					throw e;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		return "start: "+fullname;
	}	
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		String[]	pargs	= new String[]
		{
			"-gui", "false"
		};
		IExternalAccess	rootcomp	= (IExternalAccess)Starter.createPlatform(pargs).get(new ThreadSuspendable());
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(rootcomp.getServiceProvider(), IComponentManagementService.class).get(new ThreadSuspendable());
		dorun(cms, "jadex/micro/testcases/blocking/ShutdownAgent.class");
//		dorun(cms, "jadex/micro/benchmarks/MessagePerformanceAgent.class");
//		dorun(cms, "jadex/micro/examples/ping/PingScenario.application.xml");
	}
}