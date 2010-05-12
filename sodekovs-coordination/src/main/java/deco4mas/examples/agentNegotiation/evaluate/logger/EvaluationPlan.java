package deco4mas.examples.agentNegotiation.evaluate.logger;

import jadex.bdi.runtime.Plan;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import deco4mas.examples.agentNegotiation.evaluate.LogInformation;

/**
 * Evaluate the example proposals.
 */
public class EvaluationPlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		LogInformation[] info = (LogInformation[]) getBeliefbase().getBeliefSet("logInformations").getFacts();
		List<LogInformation> infoList = Arrays.asList(info);
		Set<LogInformation> infoSet = new HashSet<LogInformation>(infoList);

//		System.out.println("************ Logged ************");
//		for (LogInformation infoLog : infoSet)
//		{
//			System.out.println(infoLog.getName() + ":");
//			HashSet<Long> infoLogSet = infoLog.getValues();
//			for (Long logInfo : infoLogSet)
//			{
//				System.out.print(logInfo + " ; ");
//			}
//		}

		for (LogInformation infoLog : infoSet)
		{
			infoLog.writeDifferenz();
		}
		getBeliefbase().getBeliefSet("logInformations").removeFacts();
	}
}
