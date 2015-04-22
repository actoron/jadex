package jadex.base.test.impl;


import jadex.base.test.ComponentTestSuite;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Test if a component can be started.
 */
public class ComponentStartTest extends	ComponentTest
{
	//-------- constants --------
	
	/** The delay before the component is stopped. */
	public static final long	DELAY	= BasicService.getScaledLocalDefaultTimeout(1.0/60);
	
	//-------- constructors --------
	
	public ComponentStartTest() {
		Logger.getLogger("ComponentStartTest").log(Level.SEVERE, "ComponentSTartTest empty constructor called");
	}
	
	/**
	 *  Create a component test.
	 */
	public ComponentStartTest(IComponentManagementService cms, IModelInfo comp, ComponentTestSuite suite)
	{
		super(cms, comp, suite);
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
			
			IExternalAccess	ea	= cms.getExternalAccess(cms.getRootIdentifier().get()).get();
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					return ia.getComponentFeature(IExecutionFeature.class).waitForDelay(DELAY, false);
				}
			}).addResultListener(lis);
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					return ia.getComponentFeature(IExecutionFeature.class).waitForDelay(DELAY, true);
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