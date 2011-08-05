package jadex.launch.test;

import jadex.base.Starter;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.tools.jcc.ControlCenter;
import jadex.tools.jcc.JCCAgent;
import jadex.tools.jcc.PlatformControlCenter;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;

/**
 *  Test if all JCC plugins can be activated.
 */
public class JCCTest extends TestCase
{
	public void	testJCC()
	{
		long timeout	= 1000000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "testcases",
			"-niotransport", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}).get(sus, timeout);
		
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getService(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		
		IExternalAccess	jcc	= (IExternalAccess)cms.getExternalAccess(
			new ComponentIdentifier("jcc", platform.getComponentIdentifier())).get(sus, timeout);
		
		jcc.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final JCCAgent	jcca	= (JCCAgent)ia;
				final ControlCenter	cc	= jcca.getControlCenter();
				
				final Future	ret	= new Future();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						PlatformControlCenter	pcc	= cc.getPCC();
						IControlCenterPlugin[]	plugins	= pcc.getPlugins();
						activatePlugins(jcca, pcc, plugins, 0).addResultListener(new DelegationResultListener(ret));
					}
				});
				return ret;
			}
		}).get(sus, timeout*10);
		
		platform.killComponent().get(sus, timeout);
	}
	
	/**
	 *  Activate all plugins recursively.
	 */
	protected IFuture	activatePlugins(final JCCAgent jcca, final PlatformControlCenter pcc, final IControlCenterPlugin[] plugins, final int i)
	{
		IFuture	ret;
		if(i<plugins.length)
		{
			final Future	fut	= new Future();
			ret	= fut;
//			System.out.println("Activating plugin: "+plugins[i]);
			pcc.getPanel().setPerspective(plugins[i]).addResultListener(new SwingDelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					jcca.waitFor(500, new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									activatePlugins(jcca, pcc, plugins, i+1).addResultListener(new DelegationResultListener(fut));
								}
							});
							return null;
						}
					});
				}
			});
		}
		else
		{
			ret	= IFuture.DONE;
		}
		return ret;
	}
}
