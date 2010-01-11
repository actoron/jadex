package jadex.wfms.client;

public class WorkitemEvent
{
	private IWorkitem workitem;
	
	public WorkitemEvent(IWorkitem workitem)
	{
		this.workitem = workitem;
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
}
