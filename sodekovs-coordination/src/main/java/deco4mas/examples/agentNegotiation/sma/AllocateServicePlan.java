package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

/**
 * Allocate a service
 */
public class AllocateServicePlan extends Plan
{
	public void body()
	{
		if (getBeliefbase().getBelief("currentSa").getFact() != null)
		{
			IComponentIdentifier currentSa = (IComponentIdentifier) getBeliefbase().getBelief("currentSa").getFact();
			
			IGoal request = (IGoal) getReason();
			
			IGoal serviceAllocate = createGoal("rp_initiate");
			serviceAllocate.getParameter("action").setValue(request.getParameter("action").getValue());
			serviceAllocate.getParameter("receiver").setValue(currentSa);

			Boolean result = false;
			try
			{
//				System.out.println(this.getComponentIdentifier().getLocalName() + " -> " + currentSa);
				dispatchSubgoalAndWait(serviceAllocate);
				result = (Boolean)serviceAllocate.getParameter("result").getValue();
			} catch (GoalFailureException gfe)
			{
				result = Boolean.FALSE;
			}
			
			if (result)
			{
				getParameter("result").setValue(Boolean.TRUE);
				// more trust to Sa
				ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
				history.addEvent(currentSa, getClock().getTime(), 0.5);
			}
			else
			{
				System.out.println(this.getComponentIdentifier().getLocalName() + " Sa: " + currentSa + " no/false response! Assign new!");
//				getParameter("result").setValue(Boolean.FALSE);
				ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
				history.addEvent(currentSa, getClock().getTime(), -1.0);
				
				startAtomic();
				getBeliefbase().getBelief("currentSa").setFact(null);
				if (!(Boolean) getBeliefbase().getBelief("searchingSa").getFact())
				{
					getBeliefbase().getBelief("searchingSa").setFact(Boolean.TRUE);
					dispatchTopLevelGoal(createGoal("assignSa"));
				}
				endAtomic();
				waitForCondition("currentSaPresent");
				body();
			}
		} else
		{
			System.out.println(this.getComponentIdentifier().getLocalName() + " No Sa assigned! Assign new!");
//			getParameter("result").setValue(Boolean.FALSE);
			
			startAtomic();
			if (!(Boolean) getBeliefbase().getBelief("searchingSa").getFact())
			{
				getBeliefbase().getBelief("searchingSa").setFact(Boolean.TRUE);
				dispatchTopLevelGoal(createGoal("assignSa"));
			}
			endAtomic();
			waitForCondition("currentSaPresent");
			body();
		}
		
	}
}
