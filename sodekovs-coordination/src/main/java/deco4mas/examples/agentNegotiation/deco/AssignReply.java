package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;

public class AssignReply
{
	private IComponentIdentifier initiator;
	private String serviceType = "default";
	private IComponentIdentifier chosenOne;

	public AssignReply(IComponentIdentifier initiator, String serviceType, IComponentIdentifier chosenOne)
	{
		this.initiator = initiator;
		this.serviceType = serviceType;
		this.chosenOne = chosenOne;
	}

	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	public String getServiceType()
	{
		return serviceType;
	}
	
	public IComponentIdentifier getChosenOne()
	{
		return chosenOne;
	}
	
}
