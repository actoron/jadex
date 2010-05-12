package deco4mas.examples.agentNegotiation.deco;

public class ServiceOffer
{
	private Integer id;

	private Long deadline = 0l;

	private String serviceType = "default";

	public ServiceOffer(Integer id, Long deadline, String service)
	{
		this.id = id;
		this.deadline = deadline;
		this.serviceType = service;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Long getDeadline()
	{
		return deadline;
	}

	public void setDeadline(Long deadline)
	{
		this.deadline = deadline;
	}

	public String getServiceType()
	{
		return serviceType;
	}

	public void setServiceType(String serviceType)
	{
		this.serviceType = serviceType;
	}

}
