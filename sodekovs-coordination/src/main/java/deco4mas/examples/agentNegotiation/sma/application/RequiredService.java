package deco4mas.examples.agentNegotiation.sma.application;

import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;

public class RequiredService
{
	private static Integer id = 0;
	private IComponentIdentifier owner;
	private IComponentIdentifier sa;
	private ServiceType serviceType;
	private Boolean searching;
	private Boolean remove = false;

	private Object monitor = new Object();
	private Integer identNumber;

	public RequiredService(IComponentIdentifier owner, IComponentIdentifier sa, ServiceType serviceType)
	{
		synchronized (id)
		{
			this.owner = owner;
			this.sa = sa;
			this.serviceType = serviceType;
			searching = true;
			identNumber = id;
			id++;
		}
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

	public void setSearching(Boolean searching)
	{
		this.searching = searching;
	}

	public Boolean isSearching()
	{
		return searching;
	}

	public Object getMonitor()
	{
		return monitor;
	}

	public void remove()
	{
		remove = true;
	}

	public Boolean isRemoved()
	{
		return remove;
	}

	public Integer getId()
	{
		return identNumber;
	}

}
