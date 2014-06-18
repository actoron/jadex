package jadex.launch.test;

import jadex.base.Starter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SNonAndroid;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.micro.IPojoMicroAgent;
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
//		System.err.println("starting platform");
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
//			"-logging", "true",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-printpass", "false"});
		
		long timeout	= BasicService.getLocalDefaultTimeout();
		ISuspendable	sus	= 	new ThreadSuspendable();
		
		IExternalAccess	platform	= fut.get(sus, timeout);
		
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);

		IExternalAccess	jcc	= (IExternalAccess)cms.getExternalAccess(
			new ComponentIdentifier("jcc", platform.getComponentIdentifier())).get(sus, timeout);
		
		jcc.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				final JCCAgent	jcca	= (JCCAgent)((IPojoMicroAgent)ia).getPojoAgent();
				final ControlCenter	cc	= jcca.getControlCenter();
				
				final Future<Void>	ret	= new Future<Void>();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						PlatformControlCenter	pcc	= cc.getPCC();
						IControlCenterPlugin[]	plugins	= pcc.getPlugins();
						activatePlugins(ia, pcc, plugins, 0).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
				return ret;
			}
		}).get(sus, timeout*10);
		
		platform.killComponent().get(sus, timeout);
		
		fut	= null;
		platform	= null;
		jcc	= null;
		cms	= null;
		
		SNonAndroid.clearAWT();
		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
	}
	
	/**
	 *  Activate all plugins recursively.
	 */
	protected IFuture<Void>	activatePlugins(final IInternalAccess jcca, final PlatformControlCenter pcc, final IControlCenterPlugin[] plugins, final int i)
	{
		IFuture<Void>	ret;
		if(i<plugins.length)
		{
			final Future<Void>	fut	= new Future<Void>();
			ret	= fut;
			pcc.getPanel().setPerspective(plugins[i]).addResultListener(new SwingDelegationResultListener<Void>(fut)
			{
				public void customResultAvailable(Void result)
				{
					jcca.waitForDelay(500, new IComponentStep<Void>()
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
					}, true);
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
		for(int i=0; i<10; i++)
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
