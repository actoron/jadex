package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class ServiceContract
{
	private ServiceType service;
	private IComponentIdentifier initiator;
	private IComponentIdentifier participant;
	private ServiceBid bid;

	public ServiceContract(ServiceType service, ServiceBid bid, IComponentIdentifier initiator, IComponentIdentifier participant)
	{
		this.service = service;
		this.bid = bid;
		this.initiator = initiator;
		this.participant = participant;
	}

	public ServiceType getServiceType()
	{
		return service;
	}

	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	public IComponentIdentifier getParticipant()
	{
		return participant;
	}

	public ServiceBid getServiceBid()
	{
		return bid;
	}

	@Override
	public String toString()
	{
		return "Contract(" + service + " , " + initiator + " , " + participant + ")";
	}

}
