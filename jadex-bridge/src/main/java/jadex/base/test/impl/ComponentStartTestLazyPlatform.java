package jadex.base.test.impl;


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.test.IAbortableTestSuite;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;

/**
 *  Test if a component can be started.
 *  This version does not need a Platform on instantiation, but rather later, before tests are started.
 */
public class ComponentStartTestLazyPlatform extends	ComponentTestLazyPlatform
{
	//-------- attributes --------

	/** The delay after which the started component is stopped again. */
	// Extra to super.timeout, because timeout is used by super class also to stop init...
	public long delay;

	//-------- constructors --------

	public ComponentStartTestLazyPlatform()
	{
		Logger.getLogger("ComponentStartTest").log(Level.SEVERE, "ComponentSTartTest empty constructor called");
	}

	/**
	 *  Create a component test.
	 */
	public ComponentStartTestLazyPlatform(String comp, IAbortableTestSuite suite)
	{
		super(comp, suite);
		// Hack??? 
		delay = 500;	// Do not use scaled default timeout, because delay of Timeout.NONE makes no sense.
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a component has been started.
	 *  @param cid	The cid, set as soon as known.
	 */
	protected void componentStarted(ITuple2Future<IComponentIdentifier, Map<String, Object>> fut)
	{
		try
		{
			final IComponentIdentifier	cid	= fut.getFirstResult();
			
			// Wait some time (simulation and real time) and kill the component afterwards.
			final IResultListener<Void>	lis	= new CounterResultListener<Void>(1, new DefaultResultListener<Void>()
			{
				public synchronized void resultAvailable(Void result)
				{
//					if(cid.getName().indexOf("ParentProcess")!=-1)
//						System.out.println("destroying "+cid);
					IComponentManagementService	mycms	= cms;
					if(mycms!=null)
					{
//						if(cid.getName().indexOf("ParentProcess")!=-1)
//							System.out.println("destroying1 "+cid);
						try
						{
							mycms.destroyComponent(cid).get();
						}
						catch(ComponentTerminatedException e)
						{
							// ignore, if agent killed itself already
						}
//						if(cid.getName().indexOf("ParentProcess")!=-1)
//							System.out.println("destroying2 "+cid);
					}
				}

				@Override
				public void exceptionOccurred(Exception exception) {
					System.err.println("COULD NOT STOP COMPONENT!! Exception:");
					super.exceptionOccurred(exception);
				}
			});
//			{
//				@Override
//				public void resultAvailable(Void result)
//				{
////					if(cid.getName().indexOf("ParentProcess")!=-1)
//						System.out.println("waiting returned for "+cid);
//					super.resultAvailable(result);
//				}
//			};
			
			IExternalAccess	ea	= cms.getExternalAccess(cms.getRootIdentifier().get()).get();
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
//					if(cid.getName().indexOf("ParentProcess")!=-1)
//						System.out.println("waiting false for "+cid);
					return ia.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, false);
				}
			}).addResultListener(lis);
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
//					if(cid.getName().indexOf("ParentProcess")!=-1)
//						System.out.println("waiting true for "+cid);
					return ia.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, true);
				}
			}).addResultListener(lis);
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

	/**
	 *  Optional checking after component has finished.
	 *  @param res	The results.
	 */
	protected void checkTestResults(Map<String, Object> res)
	{
		// Nop.
	}
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return "start: "+super.toString();
	}
}