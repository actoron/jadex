package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;
import jadex.wfms.client.IClientActivity;

public class RequestTerminateActivity implements IComponentAction
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
