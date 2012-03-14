package jadex.base.gui.plugin;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

import java.awt.Component;

/**
 *  Static helper methods for JCC plugins.
 */
public class SJCC
{
	public static void	killPlattform(IExternalAccess exta, Component ui)
	{
		getRootAccess(exta).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object result)
			{
				((IExternalAccess)result).killComponent();
			}
		});
	}
	
	/**
	 *  Method to get an external access for the platform component (i.e. root component).
	 *  @param access	Any component on the platform.
	 */
	public static IFuture	getRootAccess(final IExternalAccess access)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				// Cannot use access getComponentIdentifier only as during JCC init parent can not be accessed from outside.
				
//				getRootIdentifier(cms, access.getParent()!=null ? access.getParent() : access.getComponentIdentifier())
//					.addResultListener(new SwingDelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result) throws Exception
//					{
						cms.getExternalAccess((IComponentIdentifier)access.getComponentIdentifier().getRoot())
							.addResultListener(new SwingDelegationResultListener(ret));
//					}
//				});
			}
		});
		return ret;
	}
	
	/**
	 *  Internal method to get the root identifier.
	 */
	protected static IFuture	getRootIdentifier(final IComponentManagementService cms, final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		cms.getParent(cid).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(result==null)
				{
					ret.setResult(cid);
				}
				else
				{
					getRootIdentifier(cms, (IComponentIdentifier)result)
						.addResultListener(new SwingDelegationResultListener(ret));
				}
			}
		});
		
		return ret;
	}
}
