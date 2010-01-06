package jadex.bdi.wfms.ontology;

import jadex.adapter.base.fipa.IAgentAction;
import jadex.adapter.base.fipa.SFipa;

public class RequestAuth implements IAgentAction
{
	/** The user name */
	private String userName;
	
	/** Authentication Token */
	private Object authToken;
	
	public RequestAuth()
	{
	}
	
	public RequestAuth(String userName, Object authToken)
	{
		this.userName = userName;
		this.authToken = authToken;
	}
	
	/**
	 * Sets the user name.
	 * 
	 * @param userName the user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	/**
	 * Returns the user name.
	 * 
	 * @return user name
	 */
	public String getUserName()
	{
		return userName;
	}
	
	/**
	 * Sets the authentication token.
	 * 
	 * @param authToken authentication token
	 */
	public void setAuthToken(Object authToken)
	{
		this.authToken = authToken;
	}
	
	/**
	 * Returns the authentication token.
	 * 
	 * @return authentication token
	 */
	public Object getAuthToken()
	{
		return authToken;
	}
}
