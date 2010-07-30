package deco4mas.examples.agentNegotiation.common.trustInformation;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import jadex.bridge.IComponentIdentifier;

public class TrustExecutionInformation extends TrustInformation
{
	private IComponentIdentifier sa;
	private ServiceType serviceType;
	private IComponentIdentifier sma;

	public TrustExecutionInformation(IComponentIdentifier sa, IComponentIdentifier sma, ServiceType serviceType, TrustEvent event, Long time)
	{
		super(event, time);
		this.sa = sa;
		this.sma = sma;
		this.serviceType = serviceType;
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

}
