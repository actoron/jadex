package deco4mas.examples.agentNegotiation.common.dataObjects;

import deco4mas.examples.agentNegotiation.common.statistics.Sum;
import deco4mas.examples.agentNegotiation.sma.application.workflow.management.evaluate.PenaltyFunction;
import jadex.bridge.IComponentIdentifier;

public class WorkflowData
{
	private static Integer id = 0;
	private Integer ident;
	private IComponentIdentifier identifier;
	private Long startTime;
	private Long startExecutionTime;
	private Long endTime;
	private Long intendedWorkflowTime;
	private Integer numberOfNegotiations = 0;
	private Sum costs;
	private Double profit;
	private Double negotiationCosts;
	private PenaltyFunction contratPenalty;
	
	public WorkflowData(IComponentIdentifier identifier, Long intendedWorkflowTime, Double profit, Double negotiationCosts)
	{
		this.identifier = identifier;
		this.intendedWorkflowTime = intendedWorkflowTime;
		this.profit = profit;
		this.negotiationCosts = negotiationCosts;
		this.contratPenalty = new PenaltyFunction(intendedWorkflowTime, profit);
		costs = new Sum();
		ident = id;
		id++;
	}
	
	/**
	 * used to ignore first workflow
	 * @return
	 */
	public Integer getId()
	{
		return ident;
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
		Long result = -1L;
		try
		{
			result = endTime - startExecutionTime;
		} catch (Exception e)
		{
			//omit this, "not finished"
		}
		return result;
	}
	
	public Long getNegotiationTime()
	{
		Long result = -1L;
		try
		{
			result = startExecutionTime - startTime;
		} catch (Exception e)
		{
			//omit this, "not finished"
		}
		return result;
	}
	
	public Long getCompleteTime()
	{
		Long result = -1L;
		try
		{
			result =  endTime - startTime;
		} catch (Exception e)
		{
			//omit this, "not finished"
		}
		return result;
	}
	
	public Double getNegotiationCosts()
	{
		return negotiationCosts;
	}

	public Integer getNumberOfNegotiations()
	{
		return numberOfNegotiations;
	}

	public void addNegotiation()
	{
		numberOfNegotiations++;
	}

	public Double getCosts()
	{
		return costs.getResult();
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
		Long completeTime = getCompleteTime();
		Long overrunTime = completeTime - intendedWorkflowTime;
		if (overrunTime < 0L) overrunTime = 0L;
		return contratPenalty.getPenalty(overrunTime);
	}
	
	
	
	
	
	

}
