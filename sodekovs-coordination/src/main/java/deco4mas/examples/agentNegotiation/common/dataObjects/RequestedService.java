package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bridge.IComponentIdentifier;

public class RequestedService
{
	public static String SEARCHING = "searching";
	public static String DESIGNATED = "designated";
	public static String FOUND = "found";

	private static Integer id = new Integer(0);
	private Integer identNumber;
	private IComponentIdentifier sma;
	private IComponentIdentifier sa;
	private Boolean designatedSa;
	private ServiceType serviceType;
	private String state = SEARCHING;

	private Object monitor = new Object();

	public RequestedService(IComponentIdentifier sma, ServiceType serviceType)
	{
		synchronized (id)
		{
			this.sma = sma;
			this.serviceType = serviceType;
			identNumber = id;
			id++;
		}
	}

	public Boolean getDesignatedSa()
	{
		return designatedSa;
	}

	public void setDesignatedSa(Boolean designatedSa)
	{
		this.designatedSa = designatedSa;
		if (designatedSa)
		{
			state = DESIGNATED;
		} else
		{
			state = SEARCHING;
		}

	}

	public Integer getId()
	{
		return id;
	}

	public String getState()
	{
		return state;
	}

	public IComponentIdentifier getSma()
	{
		return sma;
	}

	public void setSa(IComponentIdentifier sa)
	{
		this.sa = sa;
		state = FOUND;
	}

	public IComponentIdentifier getSa()
	{
		return sa;
	}

	public ServiceType getServiceType()
	{
		return serviceType;
	}

	public Object getMonitor()
	{
		return monitor;
	}

	@Override
	public String toString()
	{
		return "RequiredService(" + identNumber + " , " + sma + " , " + serviceType + " , " + state + " , " + designatedSa + " , " + sa
			+ ")";
	}
}
