package jadex.base.test.impl;


import jadex.base.Starter;
import jadex.base.test.ComponentTestSuite;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
import junit.framework.TestCase;

/**
 *  Test if a component can be started.
 */
public class ComponentStartTest extends	TestCase
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
	public ComponentStartTest(IComponentManagementService cms, IModelInfo comp, ComponentTestSuite suite)
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
	
//	/**
//	 *  Test the component.
//	 */
//	public void run(TestResult result)
//	{
//		if(suite.isAborted())
//		{
//			return;
//		}
//		
//		result.startTest(this);
//		
//		// Start the component.
//		try
//		{
////			System.out.println("starting: "+comp.getFilename());
//			IComponentIdentifier	cid	= cms.createComponent(null, comp.getFilename(), new CreationInfo(comp.getResourceIdentifier()), null).get(new ThreadSuspendable());
//			try
//			{
////				if(comp.getFilename().indexOf("Heatbugs")!=-1)
////				{
////					System.out.println("killing: "+comp.getFilename());
////					SyncExecutionService.DEBUG	= true;
////				}
//				cms.destroyComponent(cid).get(new ThreadSuspendable());
////				System.out.println("killed: "+comp.getFilename());
//			}
//			catch(ComponentTerminatedException cte)
//			{				
//				// Ignore if component already terminated.
//			}
//			catch(RuntimeException e)
//			{
//				// Ignore if component already terminated.
//				if(!(e.getCause() instanceof ComponentTerminatedException))
//				{
//					throw e;
//				}
//			}
//		}
//		catch(Exception e)
//		{
//			result.addError(this, e);
//		}
//
//		result.endTest(this);
//	}
	
	/**
	 *  Test the component.
	 */
	public static void dorun(IComponentManagementService cms, String filename)
	{
		// Start the component.
		try
		{
//			System.out.println("starting: "+comp.getFilename());
			IComponentIdentifier	cid	= cms.createComponent(null, filename, null, null).get(new ThreadSuspendable());
			try
			{
//				if(comp.getFilename().indexOf("Heatbugs")!=-1)
//				{
//					System.out.println("killing: "+comp.getFilename());
//					SyncExecutionService.DEBUG	= true;
//				}
				cms.destroyComponent(cid).get(new ThreadSuspendable());
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
		return "start: "+comp.getFullName();
	}	
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		IExternalAccess	rootcomp	= (IExternalAccess)Starter.createPlatform(args).get(new ThreadSuspendable());
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(rootcomp.getServiceProvider(), IComponentManagementService.class).get(new ThreadSuspendable());
		dorun(cms, "jadex/micro/benchmarks/MessagePerformanceAgent.class");
	}
}