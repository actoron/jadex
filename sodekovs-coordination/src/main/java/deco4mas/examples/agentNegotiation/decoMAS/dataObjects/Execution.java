package deco4mas.examples.agentNegotiation.decoMAS.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class Execution
{
	private IComponentIdentifier sa;
	private ServiceType serviceType;
	private IComponentIdentifier sma;
	private TrustEvent event;
	private Long time;

	// private ExecutionInformation informations;

	public Execution(IComponentIdentifier sa, IComponentIdentifier sma, ServiceType serviceType, TrustEvent event, Long time)
	{
		this.sa = sa;
		this.sma = sma;
		this.serviceType = serviceType;
		this.event = event;
		this.time = time;
	}

	public IComponentIdentifier getSa()
	{
		return sa;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public IComponentIdentifier getSma()
	{
		return sma;
	}

	public TrustEvent getEvent()
	{
		return event;
	}

	public Long getTime()
	{
		return time;
	}

}
