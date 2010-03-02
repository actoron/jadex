package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RequestFinishActivity implements IComponentAction
{
	private IClientActivity activity;
	
	public RequestFinishActivity()
	{
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
