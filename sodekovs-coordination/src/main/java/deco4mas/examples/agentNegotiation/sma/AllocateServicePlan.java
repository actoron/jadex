package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.deco.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;
import deco4mas.examples.agentNegotiation.sma.strategy.HistorytimeTrustFunction;

/**
 * Allocate a service
 */
public class AllocateServicePlan extends Plan
{
	private HistorytimeTrustFunction trustFunction;

	private void initTrust()
	{
		ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
		// trustFunc
		Map<TrustEvent, Double> eventWeight = new HashMap<TrustEvent, Double>();
		eventWeight.put(TrustEvent.SuccessfullRequest, 1.0);
		eventWeight.put(TrustEvent.FailedRequest, -6.0);
		eventWeight.put(TrustEvent.CancelArrangement, -1.0);

		trustFunction = new HistorytimeTrustFunction(this.getComponentIdentifier(), history, eventWeight);
	}

	public void body()
	{
		initTrust();
		try
		{
			IGoal request = (IGoal) getReason();
			Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
			ParameterLogger saUseLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("SaUsage_"
				+ ((ServiceType) getBeliefbase().getBelief("allocatedService").getFact()).getName());

			if (getBeliefbase().getBelief("currentSa").getFact() != null)
			{
				IComponentIdentifier currentSa = (IComponentIdentifier) getBeliefbase().getBelief("currentSa").getFact();

				IGoal serviceAllocate = createGoal("rp_initiate");
				serviceAllocate.getParameter("action").setValue(request.getParameter("action").getValue());
				serviceAllocate.getParameter("receiver").setValue(currentSa);

				Boolean result = false;
				try
				{
					// System.out.println(this.getComponentIdentifier().getLocalName()
					// + " -> " + currentSa);
					dispatchSubgoalAndWait(serviceAllocate);
					result = (Boolean) serviceAllocate.getParameter("result").getValue();
				} catch (GoalFailureException gfe)
				{
					// gfe.printStackTrace();
					result = Boolean.FALSE;
				}

				if (result)
				{
					getParameter("result").setValue(Boolean.TRUE);
					smaLogger.info("successfull execution with " + currentSa.getLocalName());

					Object[] param = new Object[4];
					param[0] = ClockTime.getStartTime(getClock());
					param[1] = getTime();
					param[2] = currentSa;
					param[3] = new Integer(0);
					saUseLogger.gnuInfo(param, "");

					// more trust to Sa
					ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
					smaLogger.info("history: " + currentSa.getLocalName() + " +Succ");
					history.addEvent(currentSa, getClock().getTime(), TrustEvent.SuccessfullRequest);
					ValueLogger.addValue("succ_" + currentSa.getLocalName(), 1.0);
					trustFunction.logTrust(getTime());
				} else
				{
					System.out.println(this.getComponentIdentifier().getLocalName() + " Sa: " + currentSa
						+ " no/false response! Assign new!");
					workflowLogger.info("No/False response from " + currentSa.getLocalName() + " at " + this.getComponentName());
					// getParameter("result").setValue(Boolean.FALSE);
					smaLogger.info("error in execution with " + currentSa.getLocalName());

					Object[] param = new Object[4];
					param[0] = ClockTime.getStartTime(getClock());
					param[1] = getTime();
					param[2] = currentSa;
					param[3] = new Integer(1);
					saUseLogger.gnuInfo(param, "");

					ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
					smaLogger.info("history: " + currentSa.getLocalName() + " -Fail");
					history.addEvent(currentSa, getClock().getTime(), TrustEvent.FailedRequest);
					ValueLogger.addValue("fail_" + currentSa.getLocalName(), 1.0);
					trustFunction.logTrust(getTime());

					startAtomic();
//					getBeliefbase().getBelief("currentSa").setFact(null);
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
				// getParameter("result").setValue(Boolean.FALSE);
				workflowLogger.info("No sa signed at " + this.getComponentName());
				smaLogger.info("no sa for execution signed");

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
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
