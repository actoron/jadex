package deco4mas.examples.agentNegotiation.evaluate.logger;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import deco4mas.examples.agentNegotiation.evaluate.LogInformation;

/**
 * Add Information to beliefbase
 */
public class InformationReceivePlan extends Plan
{
	public void body()
	{
		IGoal event = (IGoal) getReason();
		String name = (String) event.getParameter("id").getValue();
		Long value = (Long) event.getParameter("value").getValue();
		LogInformation[] comp = (LogInformation[]) getBeliefbase().getBeliefSet("logInformations").getFacts();
		List<LogInformation> compList = Arrays.asList(comp);
		Set<LogInformation> compSet = new HashSet<LogInformation>(compList);

		LogInformation logInfo = null;
		for (LogInformation log : compSet)
		{
			if (log.getName().equals(name))
				logInfo = log;
		}

		if (logInfo == null)
		{
			logInfo = new LogInformation(name);
			getBeliefbase().getBeliefSet("logInformations").addFact(logInfo);
		}

		logInfo.addValue(value);
	}
}
