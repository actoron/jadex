package jadex.wfms.bdi.ontology;

import jadex.wfms.client.IWorkitem;

public class InformWorkitemRemoved
{
	private IWorkitem workitem;
	
	public InformWorkitemRemoved()
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
