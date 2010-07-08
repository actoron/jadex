package deco4mas.examples.agentNegotiation.decoMAS.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class AssignReply
{
	private IComponentIdentifier initiator;
	private ServiceType serviceType;
	private IComponentIdentifier chosenOne;
	private Integer id;

	public AssignReply(IComponentIdentifier initiator, ServiceType serviceType, IComponentIdentifier chosenOne, Integer id)
	{
		this.initiator = initiator;
		this.serviceType = serviceType;
		this.chosenOne = chosenOne;
		this.id = id;
	}

	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public IComponentIdentifier getChosenOne()
	{
		return chosenOne;
	}

	public Integer getId()
	{
		return id;
	}

}
