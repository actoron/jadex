package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.sma.strategy.ISelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.strategy.IUtilityFunction;

// TODO Medium configuration like Deadline

public class AssignRequest
{
	private IComponentIdentifier owner;
	private ServiceType serviceType;
	private IUtilityFunction utilityFunction;
	private ISelectionStrategy selector;
	private String medium;
	private RequestInformation informations;

	public AssignRequest(IComponentIdentifier owner, IUtilityFunction utilityFunction, ISelectionStrategy selector,
		ServiceType serviceType, String medium)
	{
		this.owner = owner;
		this.utilityFunction = utilityFunction;
		this.serviceType = serviceType;
		this.selector = selector;
		this.medium = medium;
	}

	public AssignRequest(IComponentIdentifier owner, ServiceType serviceType, IUtilityFunction utilityFunction,
		ISelectionStrategy selector, String medium, RequestInformation information)
	{
		this(owner, utilityFunction, selector, serviceType, medium);
		this.informations = information;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public IUtilityFunction getUtilityFunction()
	{
		return utilityFunction;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public String getMedium()
	{
		return medium;
	}

	public ISelectionStrategy getSelector()
	{
		return selector;
	}

	public Object get(String information)
	{
		return informations.get(information);
	}

}
