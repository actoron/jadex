package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;

public class AssignRequest
{
	private IComponentIdentifier owner;
	private String serviceType = "default";
	private Evaluator evaluator;
	private String medium;

	public AssignRequest(IComponentIdentifier owner, String serviceType, Evaluator evaluator, String medium)
	{
		this.owner = owner;
		this.serviceType = serviceType;
		this.evaluator = evaluator;
		this.medium = medium;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public String getServiceType()
	{
		return serviceType;
	}

	public Evaluator getEvaluator()
	{
		return evaluator;
	}

	public String getMedium()
	{
		return medium;
	}

	
}
