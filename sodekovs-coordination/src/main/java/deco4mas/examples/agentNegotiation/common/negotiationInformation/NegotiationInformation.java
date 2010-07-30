package deco4mas.examples.agentNegotiation.common.negotiationInformation;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.mechanism.ICoordinationMechanism;

public class NegotiationInformation
{
	protected Object monitor = new Object();
	protected Integer id = Integer.MIN_VALUE;
	protected String mediumType = ICoordinationMechanism.DEFAULT_REALISATIONNAME;
	protected ServiceType serviceType;

	public NegotiationInformation(Integer id, String mediumType, ServiceType serviceType)
	{
		this.id = id;
		this.mediumType = mediumType;
		this.serviceType = serviceType;
	}

	public Integer getId()
	{
		return id;
	}

	public String getMediumType()
	{
		return mediumType;
	}

	public Object getMonitor()
	{
		return monitor;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	@Override
	public String toString()
	{
		return "NegotiationInformation(" + id + " , " + mediumType + ")";
	}
}
