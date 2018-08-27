package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
		TestReport tr = new TestReport("#1", "Start agent as suspended.");
		CreationInfo ci =  new CreationInfo("donothing", null, getComponentIdentifier());
		ci.setSuspend(Boolean.TRUE);
		IExternalAccess ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		IComponentDescription desc = getComponentDescription(ea);
		
//		IComponentDescription desc = (IComponentDescription)cms.getComponentDescription(cid).get();
		
		if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not suspended: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#2", "Start agent as master.");
		ci =  new CreationInfo("donothing", null, getComponentIdentifier());
		ci.setMaster(Boolean.TRUE);
		ea = getAgent().createComponent(null, ci.setFilename( "jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isMaster())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not master: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#3", "Start agent as daemon.");
		ci =  new CreationInfo("donothing", null, getComponentIdentifier());
		ci.setDaemon(Boolean.TRUE);
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isDaemon())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not daemon: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#4", "Start agent as autoshutdown.");
		ci =  new CreationInfo("donothing", null, getComponentIdentifier());
		ci.setAutoShutdown(Boolean.TRUE);
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isAutoShutdown())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not autoshutdown: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#5", "Start agent as suspended.");
		ci =  new CreationInfo("suspend", null, getComponentIdentifier());
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
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
		
		tr = new TestReport("#6", "Start agent as master.");
		ci =  new CreationInfo("master", null, getComponentIdentifier());
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isMaster())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not master: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#7", "Start agent as daemon.");
		ci =  new CreationInfo("daemon", null, getComponentIdentifier());
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isDaemon())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not daemon: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#8", "Start agent as autoshutdown.");
		ci =  new CreationInfo("autoshutdown", null, getComponentIdentifier());
		ea = getAgent().createComponent(null, ci.setFilename("jadex/bdi/testcases/misc/Flag.agent.xml") , null).get();
		desc = getComponentDescription(ea);
		
		if(desc.isAutoShutdown())
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Component not autoshutdown: "+desc);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
	
	/**
	 *  Get the component desciption.
	 */
	protected IComponentDescription getComponentDescription(IExternalAccess ea)
	{
		return ea.scheduleStep(new IComponentStep<IComponentDescription>()
		{
			@Override
			public IFuture<IComponentDescription> execute(IInternalAccess ia)
			{
				return new Future<IComponentDescription>(ia.getDescription());
			}
		}).get();
	}
}