package jadex.wfms.client;

public class WorkitemQueueChangeEvent
{
	private IWorkitem workitem;
	
	public WorkitemQueueChangeEvent(IWorkitem workitem)
	{
		this.workitem = workitem;
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
}
