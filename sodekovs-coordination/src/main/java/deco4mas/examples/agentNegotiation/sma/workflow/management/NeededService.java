package deco4mas.examples.agentNegotiation.sma.workflow.management;

import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;

public class NeededService
{
	private IComponentIdentifier owner;
	private IComponentIdentifier sa;
	private ServiceType serviceType;
	private Boolean searching;

	public NeededService(IComponentIdentifier owner, IComponentIdentifier sa, ServiceType serviceType)
	{
		this.owner = owner;
		this.sa = sa;
		this.serviceType = serviceType;
		searching = true;
	}

	public IComponentIdentifier getSa()
	{
		return sa;
	}

	public void setSa(IComponentIdentifier sa)
	{
		this.sa = sa;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public Boolean alreadySelected()
	{
		Boolean result = false;
		if (sa != null)
			result = true;
		return result;
	}

	public Boolean getSearching()
	{
		return searching;
	}

	public void setSearching(Boolean searching)
	{
		this.searching = searching;
	}

	public Boolean isSearching()
	{
		return searching;
	}

}
