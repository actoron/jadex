package jadex.bdiv3.features.impl;

import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.commons.future.IFuture;

/**
 *  BDI execution feature adds rule engine behavior to the cycle.
 */
public class BDIExecutionComponentFeature extends ExecutionComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public BDIExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Components with autonomous behavior may override this method
	 *  to implement a recurring execution cycle.
	 *  @return true, if the execution should continue, false, if the component may become idle. 
	 */
	protected boolean executeCycle()
	{
		assert isComponentThread();
		
		// Evaluate conditions in addition to executing steps.
		boolean	again = false;
		IInternalBDIAgentFeature bdif = getComponent().getFeature(IInternalBDIAgentFeature.class);
		boolean inited = ((IInternalBDILifecycleFeature)getComponent().getFeature(ILifecycleComponentFeature.class)).isInited();
		if(inited && bdif.getRuleSystem()!=null && bdif.getRuleSystem().isEventAvailable())
		{
//			System.out.println("executeCycle.PAE start");
			IFuture<Void> fut = bdif.getRuleSystem().processAllEvents();
			if(!fut.isDone())
				getComponent().getLogger().warning("No async actions allowed.");
			again = true;
		}
		
		return again;
	}

	/**
	 *  Called before blocking the component thread.
	 */
	protected void beforeBlock()
	{
		RPlan rplan = RPlan.RPLANS.get();
		if(rplan!=null)
		{
			rplan.beforeBlock();
		}
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	protected void afterBlock()
	{
//		if(getComponent().toString().indexOf("Leaker")!=-1)
//		{
//			System.out.println("afterBlock "+Thread.currentThread());
//		}
		RPlan rplan = RPlan.RPLANS.get();
		if(rplan!=null)
		{
//			if(getComponent().toString().indexOf("Leaker")!=-1)
//			{
//				System.out.println("afterBlock 1"+Thread.currentThread());
//			}
			rplan.afterBlock();
		}
	}
	
//	/**
//	 *  Execute a component step.
//	 */
//	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step)
//	{
//		return scheduleImmediate(step);
//	}
}
