package deco4mas.examples.agentNegotiation.common.dataObjects;

public class ServiceOffer
{
	private Integer id;

	private ServiceType serviceType;

	public ServiceOffer(Integer id, ServiceType service)
	{
		this.id = id;
		this.serviceType = service;
	}

	public Integer getId()
	{
		return id;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}
}
