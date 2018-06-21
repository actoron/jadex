package jadex.wfms.guicomponents;

import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.wfms.service.IExternalWfmsService;

public class ComponentLoginPanel extends AbstractLoginPanel
{
	protected IServiceProvider provider; 
	
	public ComponentLoginPanel(IServiceProvider provider)
	{
		super();
		this.provider = provider;	
		updateWfmsList();
	}
	
	protected IFuture discoverWfms()
	{
		return SServiceProvider.getServices(provider, IExternalWfmsService.class, RequiredServiceInfo.SCOPE_GLOBAL);
	}
	
	protected IFuture getWfmsName(IExternalWfmsService wfms)
	{
		return wfms.getName();
	}
}
