package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Restart a workflow
 */
public class RestartWorkflowPlan extends Plan
{
//	static int x = 50;
//	static int rs = 1;
	public void body()
	{
		try
		{
			// LOG
			final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
			Long startTime = ClockTime.getStartTime(getClock());

			// restart until 300000 ZE (~msec)
			if ((getTime() - startTime) <= 900000000)
//			if (rs < x)
			{
				// LOG
				smaLogger.info("start new workflow");
				RequiredService[] services = (RequiredService[]) getBeliefbase().getBeliefSet("requiredServices").getFacts();
				for (RequiredService service : services)
				{
					synchronized (service.getMonitor())
					{
						service.remove();
						getBeliefbase().getBeliefSet("requiredServices").removeFact(service);
					}
				}
				getBeliefbase().getBelief("workflow").setFact(null);
				getBeliefbase().getBelief("workflowData").setFact(null);
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(false));

				// restart
				IGoal restart = createGoal("executeWorkflow");
				dispatchTopLevelGoal(restart);
				System.gc();
//				rs++;
			} else
			{
				ValueLogger.log();
				AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) getScope().getParent().getExtension("mycoordspace"));
				System.out.println("\n\n******* WORKFLOW COMPLETLY EXECUTED *******************\n\n");
				System.out.println("B:" + space.getSpaceObjectsByType("KIVSeval")[0].getProperty("Chassisbaubillig").toString() + "("+ space.getSpaceObjectsByType("KIVSeval")[0].getProperty("ChassisbaubilligFALSE").toString() + ")" + "; " +"N:" + space.getSpaceObjectsByType("KIVSeval")[0].getProperty("Chassisbaunormal").toString() +"("+ space.getSpaceObjectsByType("KIVSeval")[0].getProperty("ChassisbaunormalFALSE").toString() + ")" + ";" + "T:" +  space.getSpaceObjectsByType("KIVSeval")[0].getProperty("Chassisbauteuer").toString() + "("+ space.getSpaceObjectsByType("KIVSeval")[0].getProperty("ChassisbauteuerFALSE").toString() + ")");
				System.out.println("\n\n******* WORKFLOW COMPLETLY EXECUTED *******************\n\n");
//				rs = 1;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
