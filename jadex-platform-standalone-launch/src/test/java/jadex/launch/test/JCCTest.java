package jadex.launch.test;

import jadex.base.Starter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.future.SwingDelegationResultListener;
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
		long timeout	= 30000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		System.err.println("starting platform");
		final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "testcases_*",
			"-logging", "true",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-printpass", "false"}).get(sus, timeout);
		
		System.err.println("fetching cms");
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);

		System.err.println("fetching jcc");
		IExternalAccess	jcc	= (IExternalAccess)cms.getExternalAccess(
			new ComponentIdentifier("jcc", platform.getComponentIdentifier())).get(sus, timeout);
		
		System.err.println("activating plugins");
		jcc.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final JCCAgent	jcca	= (JCCAgent)ia;
				final ControlCenter	cc	= jcca.getControlCenter();
				
				final Future<Void>	ret	= new Future<Void>();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						PlatformControlCenter	pcc	= cc.getPCC();
						IControlCenterPlugin[]	plugins	= pcc.getPlugins();
						activatePlugins(jcca, pcc, plugins, 0).addResultListener(new DelegationResultListener<Void>(ret));
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
	protected IFuture<Void>	activatePlugins(final JCCAgent jcca, final PlatformControlCenter pcc, final IControlCenterPlugin[] plugins, final int i)
	{
		IFuture<Void>	ret;
		if(i<plugins.length)
		{
			final Future<Void>	fut	= new Future<Void>();
			ret	= fut;
			System.out.println("Activating plugin: "+plugins[i]);
			pcc.getPanel().setPerspective(plugins[i]).addResultListener(new SwingDelegationResultListener<Void>(fut)
			{
				public void customResultAvailable(Void result)
				{
					jcca.waitFor(500, new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									activatePlugins(jcca, pcc, plugins, i+1).addResultListener(new DelegationResultListener<Void>(fut));
								}
							});
							return IFuture.DONE;
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
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		for(int i=0; i<5; i++)
		{
			final int	num	= i;
			new Thread(new Runnable()
			{				
				public void run()
				{
					try
					{
						System.out.println("starting: "+num);
						JCCTest test = new JCCTest();
						test.testJCC();
						System.out.println("finished: "+num);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
