package deco4mas.examples.agentNegotiation.sma.application;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;

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

			// get required Service
			RequiredService needService = null;
			RequiredService[] services = (RequiredService[]) getBeliefbase().getBeliefSet("requiredServices").getFacts();
			for (RequiredService service : services)
			{
				needService = service;
			}
			
			// LOG
			ParameterLogger saUseLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("SaUsage_"
				+ needService.getServiceType().getName());

			// execute the requests
			Boolean result = false;
			IComponentIdentifier currentSa = null;
			if (!needService.isSearching())
			{
				synchronized (needService.getMonitor())
				{
					currentSa = needService.getContract().getParticipant();

					IGoal serviceAllocate = createGoal("rp_initiate");
					serviceAllocate.getParameter("action").setValue((String) request.getParameter("action").getValue());
					serviceAllocate.getParameter("receiver").setValue(currentSa);

					//try allocate
					try
					{
						dispatchSubgoalAndWait(serviceAllocate);
						if (!(serviceAllocate.getParameter("result").getValue() instanceof Boolean))
						{
							smaLogger.info("result false , " + currentSa.getLocalName());
							result = false;
						} else
						{
							
							result = (Boolean) serviceAllocate.getParameter("result").getValue();
							smaLogger.info("result " + result + " , " + currentSa.getLocalName());
						}
					} catch (GoalFailureException gfe)
					{
						gfe.printStackTrace();
						smaLogger.info("GoalFailureException false , " + currentSa.getLocalName());
						result = false;
					}
					// if correct execution ...
					if (result)
					{
						getParameter("result").setValue(Boolean.TRUE);
						smaLogger.info("successfull execution with " + currentSa.getLocalName());
						((WorkflowData)getBeliefbase().getBelief("workflowData").getFact()).addCost(needService.getContract().getServiceBid().getBidFactor("cost"));

						Object[] param = new Object[4];
						param[0] = ClockTime.getStartTime(getClock());
						param[1] = getTime();
						param[2] = currentSa.getLocalName();
						param[3] = new Integer(0);
						saUseLogger.gnuInfo(param, "");
					}
				}
			}

			// if false execution ...
			if (!result)
			{
				if (currentSa == null)
				{
					System.out.println(this.getComponentIdentifier().getLocalName() + ": No sa present ! Assign new!");
					workflowLogger.info("No sa at " + this.getComponentName() + " for " + serviceName);
					// getParameter("result").setValue(Boolean.FALSE);
					smaLogger.info("No sa for " + serviceName);
				} else
				{
					System.out.println(this.getComponentIdentifier().getLocalName() + ": No/False response by + " + currentSa
						+ "Assign new!");
					workflowLogger.info("No/False response by " + currentSa + " at " + this.getComponentName());
					// getParameter("result").setValue(Boolean.FALSE);
					
					((WorkflowData)getBeliefbase().getBelief("workflowData").getFact()).addCost(0.25 * needService.getContract().getServiceBid().getBidFactor("cost"));

					smaLogger.info("error in execution with " + currentSa.getLocalName());
					Object[] param = new Object[4];
					param[0] = ClockTime.getStartTime(getClock());
					param[1] = getTime();
					param[2] = currentSa.getLocalName();
					param[3] = new Integer(1);
					saUseLogger.gnuInfo(param, "");
				}

				// service still searching
				if (!needService.isSearching())
				{
					synchronized (needService.getMonitor())
					{
						needService.setSearching(true);
						getBeliefbase().getBeliefSet("requiredServices").modified(needService);
						// getBeliefbase().getBelief("currentSa").setFact(null);
					}
				}
				waitForInternalEvent("returnExecution");
				body();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
