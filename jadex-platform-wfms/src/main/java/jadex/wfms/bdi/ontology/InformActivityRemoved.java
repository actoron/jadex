package jadex.wfms.bdi.ontology;

import jadex.wfms.client.IClientActivity;

public class InformActivityRemoved
{
	private IClientActivity activity;
	
	public InformActivityRemoved()
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
