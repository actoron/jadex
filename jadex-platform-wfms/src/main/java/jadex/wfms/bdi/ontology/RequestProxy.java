package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IComponentAction;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.client.IClient;

public class RequestProxy implements IComponentAction
{
	/** The component identifier */
	private IComponentIdentifier componentIdentifier;
	
	/** The client proxy */
	private IClient clientProxy;
	
	public RequestProxy()
	{
	}
	
	public RequestProxy(IComponentIdentifier identifier)
	{
		componentIdentifier = identifier;
		clientProxy = null;
	}
	
	public void setComponentIdentifier(IComponentIdentifier componentIdentifier)
	{
		this.componentIdentifier = componentIdentifier;
	}
	
	public IComponentIdentifier getComponentIdentifier()
	{
		return componentIdentifier;
	}
	
	public void setClientProxy(IClient clientProxy)
	{
		this.clientProxy = clientProxy;
	}
	
	public IClient getClientProxy()
	{
		return clientProxy;
	}
}
