package deco4mas.examples.agentNegotiation.sa.masterSa;

public class ServiceAgentConfig
{
	private String serviceType;
	private String agentType;

	public ServiceAgentConfig(String serviceType, String agentType)
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
