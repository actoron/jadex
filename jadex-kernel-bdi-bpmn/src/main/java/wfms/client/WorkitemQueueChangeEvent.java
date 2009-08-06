package wfms.client;

public class WorkitemQueueChangeEvent
{
	private IWorkitem workitem;
	
	public WorkitemQueueChangeEvent(IWorkitem notification)
	{
		this.workitem = notification;
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
}
