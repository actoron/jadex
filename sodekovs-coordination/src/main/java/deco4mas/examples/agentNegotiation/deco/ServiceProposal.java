package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;

public class ServiceProposal
{
	private Integer id;
	private IComponentIdentifier owner = null;
	private String serviceType = "default";
	private Double bid = 0.0;
	private Double evaluation = 0.0;

	public ServiceProposal(Integer id, String service, IComponentIdentifier owner, Double bid)
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

	public String getServiceType()
	{
		return serviceType;
	}

	public Double getBid()
	{
		return bid;
	}
	
	public void setEvaluation(Double evaluation)
	{
		this.evaluation = evaluation;
	}
}
