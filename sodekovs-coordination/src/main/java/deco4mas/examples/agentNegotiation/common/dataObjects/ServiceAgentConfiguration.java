package deco4mas.examples.agentNegotiation.common.dataObjects;

public class ServiceAgentConfiguration
{
	private String serviceType;
	private String agentType;

	public ServiceAgentConfiguration(String serviceType, String agentType)
	{
		super();
		this.serviceType = serviceType;
		this.agentType = agentType;
	}

	public String getServiceType()
	{
		return serviceType;
	}

	public String getAgentType()
	{
		return agentType;
	}

}
