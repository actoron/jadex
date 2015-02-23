package jadex.bdiv3.features.impl;

import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.ExecutionComponentFeature;

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
		BDIAgentFeature bdif = (BDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
		if(bdif.isInited() && bdif.getRuleSystem()!=null && bdif.getRuleSystem().isEventAvailable())
		{
			bdif.getRuleSystem().processAllEvents();
			again = true;
		}
		
		return again;
	}

	/**
	 *  Called before blocking the component thread.
	 */
	protected void	beforeBlock()
	{
		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
		if(rplan!=null)
		{
			rplan.beforeBlock();
		}
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	protected void	afterBlock()
	{
		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
		if(rplan!=null)
		{
			rplan.afterBlock();
		}
	}
}
