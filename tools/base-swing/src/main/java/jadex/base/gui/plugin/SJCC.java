package jadex.base.gui.plugin;

import java.awt.Component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Static helper methods for JCC plugins.
 */
public class SJCC
{
	public static void	killPlattform(IExternalAccess exta, Component ui)
	{
		getRootAccess(exta).addResultListener(new SwingDefaultResultListener<IExternalAccess>(ui)
		{
			public void customResultAvailable(IExternalAccess ea)
			{
				ea.killComponent();
			}
		});
	}
	
	/**
	 *  Method to get an external access for the platform component (i.e. root component).
	 *  @param access	Any component on the platform.
	 */
	public static IFuture<IExternalAccess>	getRootAccess(final IExternalAccess access)
	{
		final Future<IExternalAccess>	ret	= new Future<IExternalAccess>();
		access.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess((IComponentIdentifier)access.getId().getRoot())
					.addResultListener(new SwingDelegationResultListener<IExternalAccess>(ret));
			}
		});
		return ret;
	}	
}
