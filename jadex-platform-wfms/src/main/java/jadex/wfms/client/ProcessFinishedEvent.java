package jadex.wfms.client;

public class ProcessFinishedEvent
{
	private String instanceId;
	
	public ProcessFinishedEvent(String instanceId)
	{
		this.instanceId = instanceId;
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
}
