package deco4mas.examples.agentNegotiation.decoMAS.dataObjects;

import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ISelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.IUtilityFunction;

public class AssignRequest
{
	private IComponentIdentifier owner;
	private ServiceType serviceType;
	private IUtilityFunction utilityFunction;
	private ISelectionStrategy selector;
	private String medium;
	private RequestInformation informations;
	private Integer id;

	public AssignRequest(IComponentIdentifier owner, IUtilityFunction utilityFunction, ISelectionStrategy selector,
		ServiceType serviceType, String medium, Integer id)
	{
		this.owner = owner;
		this.utilityFunction = utilityFunction;
		this.serviceType = serviceType;
		this.selector = selector;
		this.medium = medium;
		this.id = id;
	}

	public AssignRequest(IComponentIdentifier owner, ServiceType serviceType, IUtilityFunction utilityFunction,
		ISelectionStrategy selector, String medium, Integer id, RequestInformation information)
	{
		this(owner, utilityFunction, selector, serviceType, medium, id);
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

	public Integer getId()
	{
		return id;
	}

}
