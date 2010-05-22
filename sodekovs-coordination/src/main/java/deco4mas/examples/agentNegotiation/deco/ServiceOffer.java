package deco4mas.examples.agentNegotiation.deco;

import deco4mas.examples.agentNegotiation.ServiceType;

//TODO Negotiation Details

public class ServiceOffer
{
	private Integer id;

//	private Long deadline = 0l;

	private ServiceType serviceType;

	public ServiceOffer(Integer id, Long deadline, ServiceType service)
	{
		this.id = id;
//		this.deadline = deadline;
		this.serviceType = service;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

//	public Long getDeadline()
//	{
//		return deadline;
//	}
//
//	public void setDeadline(Long deadline)
//	{
//		this.deadline = deadline;
//	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}
}
