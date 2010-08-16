package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class RequiredService
{
	private static Integer id = new Integer(0);
	private Integer identNumber;
	private IComponentIdentifier owner;
	private ServiceContract contract = null;
	private ServiceType serviceType;
	private Boolean searching;
	private Boolean remove = false;

	private Object monitor = new Object();

	public RequiredService(IComponentIdentifier owner, ServiceType serviceType)
	{
		synchronized (id)
		{
			this.owner = owner;
			this.serviceType = serviceType;
			searching = true;
			identNumber = id;
			id++;
		}
	}

	public ServiceContract getContract()
	{
		return contract;
	}
	
	public void setContract(ServiceContract contract)
	{
		this.contract = contract;
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

	@Override
	public String toString()
	{
		return "RequiredService(" + identNumber + " , " + owner + " , " + serviceType + " , " + searching + " , " + contract + " , " + remove
			+ ")";
	}
}
