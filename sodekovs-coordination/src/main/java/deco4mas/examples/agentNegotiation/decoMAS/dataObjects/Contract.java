package deco4mas.examples.agentNegotiation.decoMAS.dataObjects;

public class Contract
{
	private ServiceOffer offer;
	private Execution execution;
	private Boolean modified;
	
	public Contract(ServiceOffer offer)
	{
		this.offer = offer;
	}

	public ServiceOffer getOffer()
	{
		return offer;
	}

	public Execution getExecution()
	{
		return execution;
		
	}

	public void setExecution(Execution execution)
	{
		modified=true;
		this.execution = execution;
	}
	
	public Boolean isModified()
	{
		return modified;
	}
	
	public void setModified()
	{
		modified = false;
	}
}
