package jadex.wfms.client;

public class ProcessEvent
{
	private String instanceId;
	
	public ProcessEvent(String instanceId)
	{
		this.instanceId = instanceId;
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
}
