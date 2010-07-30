package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class ServiceProposal
{
	private Integer id;
	private IComponentIdentifier owner = null;
	private ServiceType serviceType;
	private ServiceBid bid;

	public ServiceProposal(Integer id, ServiceType serviceType, IComponentIdentifier owner, ServiceBid bid)
	{
		this.id = id;
		this.serviceType = serviceType;
		this.bid = bid;
		this.owner = owner;
	}

	public Integer getId()
	{
		return id;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public ServiceBid getBid()
	{
		return bid;
	}

	@Override
	public String toString()
	{
		return "ServiceProposal(" + id + " , " + owner + " , " + serviceType + " , " + bid + ")";
	}
}
