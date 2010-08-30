package jadex.base.gui.plugin;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;

import java.awt.Component;

/**
 *  Static helper methods for JCC plugins.
 */
public class SJCC
{
	public static void	killPlattform(final IControlCenter jcc, final Component ui)
	{
		SServiceProvider.getService(jcc.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				Future ret = new Future();
				ret.addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final IComponentIdentifier root = (IComponentIdentifier)result;
						cms.resumeComponent(root).addResultListener(new SwingDefaultResultListener(ui)
						{
							public void customResultAvailable(Object source, Object result)
							{
								cms.destroyComponent(root);
							}
						});
					}
				});
				getRootIdentifier(jcc.getComponentIdentifier(), cms, ret);
			}
		});
	}
	
	/**
	 *  Internal method to get the root identifier.
	 */
	protected static void getRootIdentifier(final IComponentIdentifier cid, final IComponentManagementService cms, final Future future)
	{
		cms.getParent(cid).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result==null)
				{
					future.setResult(cid);
				}
				else
				{
					getRootIdentifier((IComponentIdentifier)result, cms, future);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				future.setException(exception);
			}
		});
	}

}
