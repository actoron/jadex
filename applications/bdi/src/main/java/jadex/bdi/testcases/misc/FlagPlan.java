package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;

/**
 *  Test start/termination flags work. 
 */
public class FlagPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Start agent externally as suspended.");
		CreationInfo ci =  new CreationInfo("donothing", null);
		ci.setSuspend(Boolean.TRUE);
		IExternalAccess ea = getAgent().createComponent(ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml")).get();
		IComponentDescription desc = getComponentDescription(ea);
		if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not suspended: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		
		tr = new TestReport("#1", "Start agent that has suspended flag.");
		ci =  new CreationInfo("suspend", null);
		ea = getAgent().createComponent(ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml")).get();
		desc = getComponentDescription(ea);
		if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not suspended: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
	
	/**
	 *  Get the component desciption.
	 */
	protected IComponentDescription getComponentDescription(IExternalAccess ea)
	{
		return SComponentManagementService.getDescription(ea.getId());
	}
}