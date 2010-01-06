package jadex.wfms.bdi.ontology;

import jadex.wfms.client.IWorkitem;

public class InformWorkitemAdded
{
	private IWorkitem workitem;
	
	public InformWorkitemAdded()
	{
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
	
	public void setWorkitem(IWorkitem workitem)
	{
		this.workitem = workitem;
	}
}
