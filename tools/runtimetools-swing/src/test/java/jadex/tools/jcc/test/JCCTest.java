package jadex.tools.jcc.test;

import javax.swing.SwingUtilities;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.commons.SNonAndroid;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.tools.jcc.ControlCenter;
import jadex.tools.jcc.JCCAgent;
import jadex.tools.jcc.PlatformControlCenter;

/**
 *  Test if all JCC plugins can be activated.
 */
public class JCCTest //extends TestCase
{
	@Test
	public void	testJCC()
	{
//		System.err.println("starting platform");
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
		config.setGui(true);
		config.setValue("superpeerclient", true);
		config.setValue("settings.readonly", true);
//		config.setLogging(true);
		IFuture<IExternalAccess>	fut	= Starter.createPlatform(config);
		
		long timeout = Starter.getDefaultTimeout(null);
//		ISuspendable	sus	= 	new ThreadSuspendable();
		
		IExternalAccess	platform	= fut.get(timeout);
		timeout	= Starter.getDefaultTimeout(platform.getId());
		
		IExternalAccess	jcc	= (IExternalAccess)platform.getExternalAccessAsync(
			new BasicComponentIdentifier("jcc", platform.getId())).get(timeout);
		
		jcc.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				final JCCAgent	jcca	= (JCCAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
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
		
		platform.killComponent().get(timeout*10);
		
		fut	= null;
		platform	= null;
		jcc	= null;
		
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
					jcca.getExternalAccess().waitForDelay(500, new IComponentStep<Void>()
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
					}, true)
						.addResultListener(new IResultListener<Void>()
					{
						@Override
						public void resultAvailable(Void result)
						{
							// ignore.
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
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
	 *  Add all plugins from classpath to jcc.
	 */
	protected void	addAllPlugins(final IInternalAccess jcca, final PlatformControlCenter pcc)
	{
//     	controlcenter.libservice.getClassLoader(null)
//				.addResultListener(new SwingDefaultResultListener<ClassLoader>(PlatformControlCenterPanel.this)
//			{
//				public void customResultAvailable(final ClassLoader cl)
//				{
//                	controlcenter.libservice.getAllURLs()//controlcenter.getJCCAccess().getModel().getResourceIdentifier())
//						.addResultListener(new SwingDefaultResultListener<List<URL>>(PlatformControlCenterPanel.this)
//					{
//						public void customResultAvailable(List<URL> urls)
//						{
//							IFilter ffil = new IFilter()
//							{
//								public boolean filter(Object obj)
//								{
//									String	fn	= "";
//									if(obj instanceof File)
//									{
//										File	f	= (File)obj;
//										fn	= f.getName();
//									}
//									else if(obj instanceof JarEntry)
//									{
//										JarEntry	je	= (JarEntry)obj;
//										fn	= je.getName();
//									}
//									return fn.indexOf("Plugin")!=-1 && 
//										fn.indexOf("$")==-1 && fn.indexOf("Panel")==-1;
//								}
//							};
//							IFilter cfil = new IFilter()
//							{
//								public boolean filter(Object obj)
//								{
////									System.out.println("found: "+obj);
//											
//									Class<?> cl = (Class<?>)obj;
//									boolean ret = SReflect.isSupertype(IControlCenterPlugin.class, cl) && !(cl.isInterface() || Modifier.isAbstract(cl.getModifiers()));
//									
//									if(ret)
//									{
//										// Check if already used
//										IControlCenterPlugin[] pls = controlcenter.getPlugins();
//										for(IControlCenterPlugin pl: pls)
//										{
//											if(pl.getClass().equals(obj))
//											{
//												ret = false;
//												break;
//											}
//										}
//									}
//									return ret;
//								}
//							};
//       	                	ClassChooserPanel pp = new ClassChooserPanel(ffil, cfil, urls.toArray(new URL[urls.size()]), cl);
//       	            		int res	= JOptionPane.showOptionDialog(PlatformControlCenterPanel.this, pp, "", JOptionPane.YES_NO_CANCEL_OPTION,
//	            			JOptionPane.QUESTION_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
//    	            		if(0==res)
//    	            		{
//    	            			Class<?> plcl = (Class<?>)pp.getSelectedElement();
//    	            			if(plcl!=null)
//    	            			{
//        	        				controlcenter.addPlugin(plcl);
//    	            			}
//    	            		}

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
