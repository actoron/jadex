package jadex.bdiv3.features.impl;

import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.ExecutionComponentFeature;

/**
 * 
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
	 *  Execute the executable.
	 *  @return True, if the object wants to be executed again.
	 */
	public boolean execute()
	{
		// todo: fixme
//		assert isComponentThread();
		
		// Evaluate condition before executing step.
//		boolean aborted = false;
//		if(rulesystem!=null)
//			aborted = rulesystem.processAllEvents(15);
//		if(aborted)
//			getCapability().dumpGoals();
		
		BDIAgentFeature bdif = (BDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
		if(bdif.isInited() && bdif.getRuleSystem()!=null)
			bdif.getRuleSystem().processAllEvents();
		
//		if(steps!=null && steps.size()>0)
//		{
//			System.out.println(getComponent().getComponentIdentifier()+" steps: "+steps.size()+" "+steps.get(0));
//		}
		boolean ret = super.execute();
		
//		System.out.println(getComponentIdentifier()+" after step");

		return ret || (bdif.isInited() && bdif.getRuleSystem()!=null && bdif.getRuleSystem().isEventAvailable());
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
