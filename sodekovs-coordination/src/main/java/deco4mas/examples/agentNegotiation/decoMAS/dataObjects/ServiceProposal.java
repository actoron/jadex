package deco4mas.examples.agentNegotiation.decoMAS.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class ServiceProposal
{
	private Integer id;
	private IComponentIdentifier owner = null;
	private ServiceType serviceType;
	private Bid bid;

	private Double evaluation = 0.0;

	public ServiceProposal(Integer id, ServiceType service, IComponentIdentifier owner, Bid bid)
	{
		this.id = id;
		serviceType = service;
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

	public Bid getBid()
	{
		return bid;
	}

	public void setEvaluation(Double evaluation)
	{
		this.evaluation = evaluation;
	}
}
