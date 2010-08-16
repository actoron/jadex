package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import java.util.logging.Logger;
import jadex.bdi.runtime.Plan;
import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Creates a workflow
 */
public class EvaluateWorkflowPlan extends Plan
{
	public void body()
	{
		try
		{
			Logger logger = (Logger) AgentLogger.getDataTable("Money_" + getComponentName());
			WorkflowData data = (WorkflowData) getBeliefbase().getBelief("workflowData").getFact();
			if (data.getId() != 0)
			{
				StringBuffer buf = new StringBuffer(200);
				buf.append(data.getId() + " ");
				Double money = (Double) getBeliefbase().getBelief("moneyBank").getFact();
				
				Double profit = data.getProfit();
				Double costs = data.getCosts();
				buf.append(costs + " ");
				Double negCost = (data.getNumberOfNegotiations()*data.getNegotiationCosts());
				buf.append(negCost + " ");
				Double pen = data.getContratPenalty();
				buf.append(pen + " ");
				Double workflowCosts = profit - costs- negCost - pen;
				buf.append(workflowCosts + " ");
				money += workflowCosts;
				buf.append(money + " ");
				logger.info(buf.toString());
				getBeliefbase().getBelief("moneyBank").setFact(money);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
