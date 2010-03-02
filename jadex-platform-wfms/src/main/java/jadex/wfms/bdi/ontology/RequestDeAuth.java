package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;
import jadex.base.fipa.SFipa;

public class RequestDeAuth implements IComponentAction
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
