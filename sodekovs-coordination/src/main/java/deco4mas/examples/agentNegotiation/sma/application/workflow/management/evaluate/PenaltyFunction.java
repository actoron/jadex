package deco4mas.examples.agentNegotiation.sma.application.workflow.management.evaluate;

public class PenaltyFunction
{
	Long workflowTime = 0L;
	Double workflowProfit = 0d;

	public PenaltyFunction(Long intendedWorkflowTime, Double workflowProfit)
	{
		this.workflowTime = intendedWorkflowTime;
		this.workflowProfit = workflowProfit;
	}
	public Double getPenalty(Long overrunTime)
	{
		Double result = 0.0;
		if (overrunTime > 0)
		{
//			result = workflowProfit * Math.pow(overrunTime/workflowTime.doubleValue(), 2);
			result = workflowProfit/(workflowTime*2) * overrunTime;
		}
		return result;
	}

}
