package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RequestTerminateActivity implements IAgentAction
{
	private IClientActivity activity;
	
	public RequestTerminateActivity()
	{
	}
	
	public RequestTerminateActivity(IClientActivity activity)
	{
		this.activity = activity;
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
	
	public void setActivity(IClientActivity activity)
	{
		this.activity = activity;
	}
}
