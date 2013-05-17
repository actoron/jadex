package jadex.android.clientapp.bditest;

import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanbase;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;

@Service
public class DisplayTextServiceImpl implements IDisplayTextService
{
	/** The agent. */
	@ServiceComponent
	IBDIInternalAccess agent;

	@Override
	public IFuture<Void> showUiMessage(String message)
	{
		// Change Fact in Beliefbase
		IBelief belief = agent.getBeliefbase().getBelief("HelloMessage");
		belief.setFact(message);

		// Create new SayHelloPlan
		IPlanbase planbase = agent.getPlanbase();
		IMPlan mplan = ((IMPlanbase) planbase.getModelElement()).getPlan("say_hello");
		IPlan plan = planbase.createPlan(mplan);
		plan.startPlan();

		return IFuture.DONE;
	}
}
