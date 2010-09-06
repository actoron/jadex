package deco4mas.examples.agentNegotiation.sma.application.workflow.management.evaluate;

/**
 * Function to define a money penalty
 */
public class PenaltyFunction
{
	Long workflowTime = 0L;
	Double workflowProfit = 0d;

	public PenaltyFunction(Long intendedWorkflowTime, Double workflowProfit)
	{
		this.workflowTime = intendedWorkflowTime;
		this.workflowProfit = workflowProfit;
	}
	
	/**
	 * Get Penalty for given overrun time
	 * (Must instantiated with intendedWorkflowTime and profit) 
	 * 
	 * 0.0 if no overrun time
	 * @param overrunTime time over intendedWorkflowTime
	 * @return
	 */
	public Double getPenalty(Long overrunTime)
	{
		Double result = 0.0;
		if (overrunTime > 0)
		{
//			result = workflowProfit * Math.pow(overrunTime/workflowTime.doubleValue(), 2);
			
//			result = workflowProfit/(workflowTime*2) * overrunTime;
			result = workflowProfit/(workflowTime) * overrunTime;
			
			
//			double result25 = workflowProfit/(workflowTime*1.5) * overrunTime;
//			double result3 = workflowProfit/(workflowTime) * overrunTime;
//			double result35 = workflowProfit/(workflowTime*0.5) * overrunTime;
//			System.out.println("2PEnal#" +  result  + " = " +  workflowProfit  + " - " + workflowTime + " - " +overrunTime);
//			System.out.println("2PEnal#" +  result  + " = " +  result25  + " - " + result3 + " - " +result35);
		}
		return result;
	}

}
