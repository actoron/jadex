package jadex.bdi.testcases.planlib;

import jadex.base.test.TestReport;
import jadex.bdi.planlib.protocols.InteractionState;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.TimeoutException;

/**
 *  Test different cases of protocol cancellation.
 */
public class CMTestPlan extends Plan
{
	/**
	 *  Plan body performs the tests.
	 */
	public void	body()
	{
		testInitiatorCancel();
		
		testReceiverAbort();
	}
	
	/**
	 *  Test cancellation of interaction from initiator side (this side).
	 */
	public void testInitiatorCancel()
	{
		// Create receiver agent.
		String	agenttype	= "/jadex/bdi/testcases/planlib/CMReceiver.agent.xml";
		IComponentIdentifier receiver = getAgent().createComponent(new CreationInfo(getComponentIdentifier()).setFilename(agenttype)).get().getId();
		
		// Dispatch request goal.
		IGoal	request	= createGoal("procap.rp_initiate");
		request.getParameter("action").setValue("dummy request");
		request.getParameter("receiver").setValue(receiver);
		dispatchSubgoal(request);
		
		// Wait a sec. and then drop the interaction goal.
		waitFor(1000);
		request.drop();
		
		// Wait for goal to be finished and check the result.
		try
		{
			waitForGoalFinished(request);
		}
		catch(GoalFailureException gfe){}
		InteractionState	state	= (InteractionState)request.getParameter("interaction_state").getValue();
		TestReport	report	= new TestReport("test_cancel", "Test if interaction can be cancelled on initiator side.");
		if(InteractionState.CANCELLATION_SUCCEEDED.equals(state.getCancelResponse(receiver)))
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Wrong result. Expected '"+InteractionState.CANCELLATION_SUCCEEDED+"' but was '"+state.getCancelResponse(receiver)+"'.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		// Destroy receiver agent.
		getAgent().getExternalAccess(receiver).killComponent().get();
	}
	
	/**
	 *  Test abortion of interaction by receiver side (other side).
	 */
	public void testReceiverAbort()
	{
		// Create receiver agent.
		String	agenttype	= "/jadex/bdi/testcases/planlib/CMReceiver.agent.xml";
		IComponentIdentifier receiver = getAgent().createComponent(new CreationInfo(getComponentIdentifier()).setFilename(agenttype)).get().getId();
		
		// Dispatch request goal.
		IGoal	request	= createGoal("procap.rp_initiate");
		request.getParameter("action").setValue("dummy request");
		request.getParameter("receiver").setValue(receiver);
		dispatchSubgoal(request);
		
		// Wait a sec. and then kill the receiver agent (should abort interaction in its end state).
		waitFor(1000);
		getAgent().getExternalAccess(receiver).killComponent().get();
		
		// Check if goal finishes. (todo: check result).
		TestReport	report	= new TestReport("test_abort", "Test if interaction can be aborted on receiver side.");
		try
		{
			waitForGoalFinished(request, 3000);
			report.setFailed("Goal unexpectedly succeeded.");
		}
		catch(GoalFailureException e)
		{
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setFailed("Goal did not finish.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
