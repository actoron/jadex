package jadex.bdi.wfms.ontology;

import jadex.bridge.IComponentIdentifier;
import jadex.wfms.client.IClient;

public class ComponentClientProxy implements IClient
{
	private String userName;
	
	private IComponentIdentifier componentIdentifier;
	
	public ComponentClientProxy()
	{
	}
	
	public ComponentClientProxy(String userName, IComponentIdentifier componentIdentifier)
	{
		this.userName = userName;
		this.componentIdentifier = componentIdentifier;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setComponentIdentifier(IComponentIdentifier componentIdentifier)
	{
		this.componentIdentifier = componentIdentifier;
	}
	
	public IComponentIdentifier getComponentIdentifier()
	{
		return componentIdentifier;
	}
	
	public int hashCode()
	{
		int hashCode = 0;
		hashCode = 31 * hashCode + (userName == null ? 0 : userName.hashCode());
		hashCode = 31 * hashCode + (componentIdentifier == null ? 0 : componentIdentifier.hashCode());
		return hashCode;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof ComponentClientProxy)
		{
			ComponentClientProxy other = (ComponentClientProxy) obj;
			if (other.getUserName().equals(userName) && other.getComponentIdentifier().equals(componentIdentifier))
				return true;
		}
		return false;
	}
}
