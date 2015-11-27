package jadex.launch.test;

import jadex.base.Starter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SNonAndroid;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.tools.jcc.ControlCenter;
import jadex.tools.jcc.JCCAgent;
import jadex.tools.jcc.PlatformControlCenter;

import javax.swing.SwingUtilities;

import org.junit.Test;

/**
 *  Test if all JCC plugins can be activated.
 */
public class JCCTest //extends TestCase
{
	@Test
	public void	testJCC()
	{
//		System.err.println("starting platform");
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
//			"-logging", "true",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-printpass", "false"});
		
		long timeout = Starter.getLocalDefaultTimeout(null);
//		ISuspendable	sus	= 	new ThreadSuspendable();
		
		IExternalAccess	platform	= fut.get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
		
		IComponentManagementService	cms	= (IComponentManagementService)SServiceProvider
			.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);

		IExternalAccess	jcc	= (IExternalAccess)cms.getExternalAccess(
			new BasicComponentIdentifier("jcc", platform.getComponentIdentifier())).get(timeout);
		
		jcc.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				final JCCAgent	jcca	= (JCCAgent)ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
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
		}).get(timeout*10);
		
		platform.killComponent().get(timeout);
		
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
					jcca.getComponentFeature(IExecutionFeature.class).waitForDelay(500, new IComponentStep<Void>()
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
