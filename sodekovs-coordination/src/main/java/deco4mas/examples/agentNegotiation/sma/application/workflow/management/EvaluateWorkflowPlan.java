package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.Plan;

import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Evaluate the workflow
 */
public class EvaluateWorkflowPlan extends Plan {
	public void body() {
		try {
			Logger logger = (Logger) AgentLogger.getDataTable("Money_"
					+ getComponentName(), true);
			WorkflowData data = (WorkflowData) getBeliefbase().getBelief(
					"workflowData").getFact();
			if (data.getId() != 0) {
				StringBuffer buf = new StringBuffer(200);

				double money = commaSeparated(buf, data);
				
				logger.info(buf.toString());
				getBeliefbase().getBelief("moneyBank").setFact(money);
				

				//*************************************************************
				// This is a hack for this special application.xml -> AgentNegotiation
				//: save result also to space in order to enable evaluation by automated simulation component
				AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("mycoordspace"));
				space.getSpaceObjectsByType("KIVSeval")[0].setProperty("moneyBankSMA", new Double ((Math.round(money))).intValue());
				int counter = (Integer) space.getSpaceObjectsByType("KIVSeval")[0].getProperty("executedWorkflows");
				space.getSpaceObjectsByType("KIVSeval")[0].setProperty("executedWorkflows", counter+1);
				
				System.out.println("#########################################################################################################################################");
				System.out.println(space.getSpaceObjectsByType("KIVSeval")[0].getProperty("moneyBankSMA") +  "  - " + space.getSpaceObjectsByType("KIVSeval")[0].getProperty("executedWorkflows"));
				System.out.println("#########################################################################################################################################");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	/**
	 * Separate single data by space character
	 * 
	 * @param buf
	 * @param data
	 */
	private double spaceSeparated(StringBuffer buf, WorkflowData data) {
		buf.append(data.getId() + " ");
		Double money = (Double) getBeliefbase().getBelief("moneyBank")
				.getFact();

		Double profit = data.getProfit();
		Double costs = data.getCosts();
		buf.append(costs + " ");
		Double negCost = (data.getNumberOfNegotiations() * data
				.getNegotiationCosts());
		buf.append(negCost + " ");
		Double pen = data.getContratPenalty();
		buf.append(pen + " ");
		Double workflowCosts = profit - costs - negCost - pen;
		buf.append(workflowCosts + " ");
		money += workflowCosts;
		buf.append(money + " ");
		
		return money;
	}

	/**
	 * Separate single data by comma character and round double values
	 * @param buf
	 * @param data
	 */
	private double commaSeparated(StringBuffer buf, WorkflowData data) {
		buf.append(data.getId() + ",");
		Double money = (Double) getBeliefbase().getBelief("moneyBank")
				.getFact();

		Double profit = data.getProfit();
		Double costs = data.getCosts();
		buf.append(Math.round(costs) + ",");
		Double negCost = (data.getNumberOfNegotiations() * data
				.getNegotiationCosts());
		buf.append(Math.round(negCost) + ",");
		Double pen = data.getContratPenalty();
		buf.append(Math.round(pen) + ",");
		Double workflowCosts = profit - costs - negCost - pen;
		buf.append(Math.round(workflowCosts) + ",");
		money += workflowCosts;
		buf.append(Math.round(money));
		
		return money;
	}
}
