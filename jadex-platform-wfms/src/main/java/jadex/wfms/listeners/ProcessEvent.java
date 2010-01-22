package jadex.wfms.listeners;

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
