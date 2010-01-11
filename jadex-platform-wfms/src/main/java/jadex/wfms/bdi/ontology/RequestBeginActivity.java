package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RequestBeginActivity implements IAgentAction
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
