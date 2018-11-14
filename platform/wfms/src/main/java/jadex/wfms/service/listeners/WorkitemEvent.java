package jadex.wfms.service.listeners;

import jadex.wfms.client.IWorkitem;

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
