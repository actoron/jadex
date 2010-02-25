package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IComponentAction;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RequestBeginActivity implements IComponentAction
{
	private IWorkitem workitem;
	
	public RequestBeginActivity()
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
