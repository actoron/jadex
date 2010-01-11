package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;
import jadex.adapter.base.fipa.SFipa;

public class RequestDeAuth implements IAgentAction
{
	private String userName;
	
	public RequestDeAuth()
	{
	}
	
	public RequestDeAuth(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
}
