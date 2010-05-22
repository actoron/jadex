package deco4mas.examples.agentNegotiation.deco;

import deco4mas.examples.agentNegotiation.ServiceType;
import jadex.bridge.IComponentIdentifier;

public class AssignReply
{
	private IComponentIdentifier initiator;
	private ServiceType serviceType;
	private IComponentIdentifier chosenOne;

	public AssignReply(IComponentIdentifier initiator, ServiceType serviceType2, IComponentIdentifier chosenOne)
	{
		this.initiator = initiator;
		this.serviceType = serviceType2;
		this.chosenOne = chosenOne;
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

}
