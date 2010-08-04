package deco4mas.examples.agentNegotiation.common.dataObjects;

import deco4mas.examples.agentNegotiation.common.statistics.Sum;
import deco4mas.examples.agentNegotiation.sma.application.workflow.management.evaluate.PenaltyFunction;
import jadex.bridge.IComponentIdentifier;

public class WorkflowWrapper
{
	private IComponentIdentifier identifier;
	private Long startTime;
	private Long startExecutionTime;
	private Long endTime;
	private Long intendedWorkflowTime;
	private Integer numberOfNegotiations;
	private Sum costs;
	private Double profit;
	private PenaltyFunction contratPenalty;
	
	public WorkflowWrapper(IComponentIdentifier identifier, Long intendedWorkflowTime, Double profit)
	{
		this.identifier = identifier;
		this.intendedWorkflowTime = intendedWorkflowTime;
		this.profit = profit;
		this.contratPenalty = new PenaltyFunction(intendedWorkflowTime, profit);
		costs = new Sum();
	}

	public Long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	public Long getStartExecutionTime()
	{
		return startExecutionTime;
	}

	public void setStartExecutionTime(Long startExecutionTime)
	{
		this.startExecutionTime = startExecutionTime;
	}

	public Long getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Long endTime)
	{
		this.endTime = endTime;
	}
	
	public Long getExecutionTime()
	{
		return endTime - startExecutionTime;
	}
	
	public Long getNegotiationTime()
	{
		return startExecutionTime - startTime;
	}
	
	public Long getCompleteTime()
	{
		return endTime - startTime;
	}

	public Integer getNumberOfNegotiations()
	{
		return numberOfNegotiations;
	}

	public void addNegotiations()
	{
		numberOfNegotiations++;
	}

	public Sum getCosts()
	{
		return costs;
	}

	public void addCost(Double cost)
	{
		costs.addValue(cost);
	}

	public IComponentIdentifier getIdentifier()
	{
		return identifier;
	}

	public Long getIntendedWorkflowTime()
	{
		return intendedWorkflowTime;
	}

	public Double getProfit()
	{
		return profit;
	}

	public Double getContratPenalty()
	{
		Long overrunTime = (endTime - startTime) - intendedWorkflowTime;
		if (overrunTime < 0L) overrunTime = 0L;
		return contratPenalty.getPenalty(overrunTime);
	}
	
	
	
	
	
	

}
