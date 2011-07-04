package jadex.wfms.client.standard;

import jadex.bridge.IComponentIdentifier;
import jadex.wfms.service.IExternalWfmsService;

public interface IWfmsClient
{
	/** Returns the component identifier. */
	public IComponentIdentifier getComponentIdentifier();
	
	/** Returns the WfMS-Access */
	public IExternalWfmsService getWfms();
}
