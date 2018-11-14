package jadex.wfms.client.standard;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.wfms.service.IExternalWfmsService;

public interface IWfmsClient
{
	/** Returns the component identifier. */
	public IComponentIdentifier getComponentIdentifier();
	
	/** Returns the component access. */
	public IExternalAccess getExternalAccess();
	
	/** Returns the WfMS-Access */
	public IExternalWfmsService getWfms();
}
