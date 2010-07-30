package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class ExecutedService
{
	private static Integer id = 0;
	private Integer identNumber;
	private IComponentIdentifier sma;
	private IComponentIdentifier sa;
	private ServiceType serviceType;
	private Boolean correct;
	private Long time;

	public ExecutedService(IComponentIdentifier sma, IComponentIdentifier sa, ServiceType serviceType, Boolean correct, Long time)
	{
		synchronized (id)
		{
			this.sma = sma;
			this.sa = sa;
			this.serviceType = serviceType;
			this.correct = correct;
			this.time = time;
			identNumber = id;
			id++;
		}
	}

	public Integer getId()
	{
		return identNumber;
	}

	public IComponentIdentifier getSa()
	{
		return sa;
	}

	public IComponentIdentifier getSma()
	{
		return sma;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public Boolean isCorrect()
	{
		return correct;
	}

	public Long getTime()
	{
		return time;
	}

	@Override
	public String toString()
	{
		return "ExecutedService(" + identNumber + " , " + sma + " , " + serviceType + " , " + sa + correct + ")";
	}

}
