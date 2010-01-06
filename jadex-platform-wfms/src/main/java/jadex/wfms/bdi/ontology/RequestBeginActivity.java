package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RequestBeginActivity implements IAgentAction
{
	private IClientActivity activity;
	
	private IWorkitem workitem;
	
	public RequestBeginActivity()
	{
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
	
	public void setActivity(IClientActivity activity)
	{
		this.activity = activity;
	}
	
	public void setWorkitem(IWorkitem workitem)
	{
		this.workitem = workitem;
	}
}
