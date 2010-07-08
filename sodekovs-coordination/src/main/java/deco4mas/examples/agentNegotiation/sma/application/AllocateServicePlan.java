package deco4mas.examples.agentNegotiation.sma.application;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.logging.Logger;
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

			// Sa Present
			RequiredService needService = null;
			RequiredService[] services = (RequiredService[]) getBeliefbase().getBeliefSet("requiredServices").getFacts();
			for (RequiredService service : services)
			{
				needService = service;
			}
			// LOG
			ParameterLogger saUseLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("SaUsage_"
				+ needService.getServiceType().getName());

			Boolean result = false;
			IComponentIdentifier currentSa = null;

			if (!needService.isSearching())
			{
				synchronized (needService.getMonitor())
				{
					currentSa = needService.getSa();

					IGoal serviceAllocate = createGoal("rp_initiate");
					serviceAllocate.getParameter("action").setValue((String) request.getParameter("action").getValue());
					serviceAllocate.getParameter("receiver").setValue(currentSa);

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
						param[2] = currentSa.getLocalName();
						param[3] = new Integer(0);
						saUseLogger.gnuInfo(param, "");
					}
				}
			}

			if (needService.isSearching() || !result)
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
					smaLogger.info("error in execution with " + currentSa.getLocalName());
				}
				Object[] param = new Object[4];
				param[0] = ClockTime.getStartTime(getClock());
				param[1] = getTime();
				param[2] = currentSa.getLocalName();
				param[3] = new Integer(1);
				saUseLogger.gnuInfo(param, "");

				synchronized (needService.getMonitor())
				{
					needService.setSearching(true);
					getBeliefbase().getBeliefSet("requiredServices").modified(needService);
					// getBeliefbase().getBelief("currentSa").setFact(null);
				}

				Boolean retry = false;
				while (!retry)
				{
					if (!needService.isSearching())
					{
						retry = true;
					} else
					{
						waitForInternalEvent("serviceSatisfied");
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
