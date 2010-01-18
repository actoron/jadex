package jadex.wfms.bdi.ontology;

import jadex.wfms.client.IClientActivity;

public class InformUserActivityAdded
{
	private String userName;
	
	private IClientActivity activity;
	
	public InformUserActivityAdded()
	{
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
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
