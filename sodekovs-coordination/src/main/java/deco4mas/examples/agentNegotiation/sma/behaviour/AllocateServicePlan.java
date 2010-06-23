package deco4mas.examples.agentNegotiation.sma.behaviour;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.history.ServiceAgentHistory;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.history.TrustEvent;
import deco4mas.examples.agentNegotiation.sma.workflow.management.NeededService;

/**
 * Allocate a service
 */
public class AllocateServicePlan extends Plan
{
	public void body()
	{
		try
		{
			IGoal request = (IGoal) getReason();
			// get Logger
			Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			String serviceName = (String) request.getParameter("action").getValue();

			// Sa Present
			NeededService needService = null;
			NeededService[] services = (NeededService[]) getBeliefbase().getBeliefSet("neededServices").getFacts();
			for (NeededService service : services)
			{
				needService = service;
			}
			// LOG
			ParameterLogger saUseLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("SaUsage_"
				+ needService.getServiceType().getName());

			if (needService.alreadySelected())
			{
				IComponentIdentifier currentSa = needService.getSa();

				IGoal serviceAllocate = createGoal("rp_initiate");
				serviceAllocate.getParameter("action").setValue(request.getParameter("action").getValue());
				serviceAllocate.getParameter("receiver").setValue(currentSa);

				Boolean result = false;
				try
				{
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
					((HistorytimeTrustFunction) getBeliefbase().getBelief("trustFunction").getFact()).logTrust(getTime());
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
					((HistorytimeTrustFunction) getBeliefbase().getBelief("trustFunction").getFact()).logTrust(getTime());

					startAtomic();
					// getBeliefbase().getBelief("currentSa").setFact(null);
					if (!needService.isSearching())
					{
						smaLogger.info("Send SignEnd to " + needService.getSa().getLocalName());
						IMessageEvent me = createMessageEvent("informMessage");

						List cis = new LinkedList();
						cis.add(needService.getSa());
						me.getParameter("receivers").setValue(cis);
						me.getParameter("content").setValue("sign end");
						sendMessage(me);
						needService.setSa(null);
						needService.setSearching(true);
						IGoal assignSa = createGoal("assignSa");
						assignSa.getParameter("service").setValue(needService);
						dispatchTopLevelGoal(assignSa);
					}
					endAtomic();

					Boolean retry = false;
					while (!retry)
					{
						waitForInternalEvent("serviceFound");
						if (!needService.isSearching())
						{
							retry = true;
						}
					}
					body();
				}
			} else
			{
				System.out.println(this.getComponentIdentifier().getLocalName() + " No Sa assigned! Assign new!");
				// getParameter("result").setValue(Boolean.FALSE);
				workflowLogger.info("No sa signed at " + this.getComponentName());
				smaLogger.info("no sa for execution signed");

				startAtomic();
				if (!needService.isSearching())
				{
					needService.setSearching(true);
					dispatchTopLevelGoal(createGoal("assignSa"));
				}
				endAtomic();

				Boolean retry = false;
				while (retry)
				{
					waitForInternalEvent("serviceFound");
					if (!needService.isSearching())
					{
						retry = true;
					}
				}
				body();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
