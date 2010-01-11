package jadex.wfms.bdi.ontology;

import jadex.wfms.client.IClientActivity;

public class InformActivityAdded
{
	private IClientActivity activity;
	
	public InformActivityAdded()
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
