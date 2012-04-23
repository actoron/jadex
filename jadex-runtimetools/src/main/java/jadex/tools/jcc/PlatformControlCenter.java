package jadex.tools.jcc;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.base.gui.plugin.IControlCenter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *  A control center for a single platform.
 */
public class PlatformControlCenter	implements IControlCenter, IPropertiesProvider
{
	// -------- attributes --------

	/** The platform access. */
	protected IExternalAccess	platformaccess;
	
	/** The plugins (plugin->panel). */
	protected Map<IControlCenterPlugin, JComponent>	plugins;

	/** The global control center. */
	protected ControlCenter	controlcenter;

	/** The single platform control center panel. */
	protected PlatformControlCenterPanel	pccpanel;
	
	/** The settings of the control center and all plugins. */
	protected Properties	props;
	
	/** The library service. */
	protected ILibraryService libservice;

	//-------- methods called by global control center --------

	/**
	 *  Initialize a control center.
	 */
	public IFuture<Void>	init(IExternalAccess platformaccess, final ControlCenter controlcenter, final String[] plugin_classes)
	{
		this.platformaccess = platformaccess;
		this.controlcenter	= controlcenter;
		this.plugins = new LinkedHashMap<IControlCenterPlugin, JComponent>();
		this.props	= new Properties();
		this.pccpanel	= new PlatformControlCenterPanel(this);
		
		// Load plugins.
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(controlcenter.getJCCAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingExceptionDelegationResultListener<ILibraryService, Void>(ret)
		{
			public void customResultAvailable(ILibraryService result)
			{
				libservice = result;
//				ClassLoader cl = ((ILibraryService)result).getClassLoader();
				
				// todo: what about dynamic plugin loading?
//				ClassLoader cl = controlcenter.getJCCAccess().getModel().getClassLoader();
				libservice.getClassLoader(controlcenter.getJCCAccess().getModel().getResourceIdentifier())
					.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
				{
					public void customResultAvailable(ClassLoader cl)
					{
						for(int i=0; i<plugin_classes.length; i++)
						{
							try
							{
								Class plugin_class = SReflect.classForName(plugin_classes[i], cl);
								final IControlCenterPlugin p = (IControlCenterPlugin)plugin_class.newInstance();
								plugins.put(p, null);
								
								if(p.isLazy())
								{
									setStatusText("Plugin loaded successfully: "+ p.getName());									
								}
								else
								{
									initPlugin(p).addResultListener(new SwingDefaultResultListener<Void>(pccpanel)
									{
										public void customResultAvailable(Void result)
										{
											setStatusText("Plugin loaded successfully: "+ p.getName());
										}
									});
								}
							}
							catch(Exception e)
							{
								setStatusText("Plugin error: "+plugin_classes[i]);
							}
						}
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Close all active plugins. Called when the JCC exits.
	 */
	public IFuture<Void>	dispose()
	{
		final Future<Void> ret = new Future<Void>();
		
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		// Close all plugins, which have a panel associated.
		CounterResultListener<Void> lis = new CounterResultListener<Void>(plugins.size(), true,
			new SwingDelegationResultListener<Void>(ret));
		for(Iterator it=plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
			if(plugins.get(plugin) != null)
			{
				try
				{
					plugin.shutdown().addResultListener(lis);
				}
				catch(Exception e)
				{
					System.err.println("Exception while closing JCC-Plug-In " + plugin.getName());
					e.printStackTrace();
					lis.exceptionOccurred(e);
				}
			}
			else
			{
				lis.resultAvailable(null);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the resource identifier.
	 */
	public IFuture<ClassLoader> getClassLoader(IResourceIdentifier rid)
	{
		return libservice.getClassLoader(rid==null? getJCCAccess().getModel().getResourceIdentifier(): rid);
	}
	
	/**
	 *  Get the control center panel.
	 */
	public PlatformControlCenterPanel	getPanel()
	{
		return pccpanel;
	}
	
	/**
	 *  Push plugin settings to platform and save platform properties.
	 */
	public IFuture<Void>	savePlatformProperties()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IControlCenterPlugin[]	aplugins	= getPlugins();
//		System.out.println("Pushing platform settings: "+aplugins.length);
		CounterResultListener<Void>	crl	= new CounterResultListener<Void>(aplugins.length, 
			new SwingDelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("Pushed platform settings");
				SServiceProvider.getService(getPlatformAccess().getServiceProvider(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingExceptionDelegationResultListener<ISettingsService, Void>(ret)
				{
					public void customResultAvailable(ISettingsService settings)
					{
//						System.out.println("Fetched settings service");
//						ISettingsService	settings	= (ISettingsService)result;
						settings.saveProperties().addResultListener(new SwingDelegationResultListener<Void>(ret));
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						// No settings service: ignore.
						ret.setResult(null);
					}
				});
			}
		});
		
		try
		{
			for(int i=0; i<aplugins.length; i++)
			{
				if(plugins.get(aplugins[i])!=null)
				{
//					System.out.println("Pushing platform settings: "+aplugins[i].getName());
					aplugins[i].pushPlatformSettings().addResultListener(crl);
				}
				else
				{
//					System.out.println("Not pushing platform settings: "+aplugins[i].getName());
					crl.resultAvailable(null);
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		return ret;
	}
	
	//-------- methods called by platform control center panel --------
	
	/**
	 *  Get the global control center.
	 */
	public ControlCenter	getControlCenter()
	{
		return controlcenter;
	}
	
	/**
	 *  Get all plugins.
	 */
	public IControlCenterPlugin[]	getPlugins()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		
		return (IControlCenterPlugin[])plugins.keySet().toArray(new IControlCenterPlugin[plugins.size()]);
	}
	
	/**
	 * Find a plugin by name.
	 * 
	 * @return null, when plugin is not found.
	 */
	public IControlCenterPlugin getPluginForName(String name)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		for(Iterator it=plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
			if(name.equals(plugin.getName()))
				return plugin;
		}
		return null;
	}

	
	/**
	 *  Activate a plugin.
	 */
	public IFuture<Void> activatePlugin(final IControlCenterPlugin plugin)
	{
//		System.out.println("activate plugin: "+plugin);
		
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		final Future<Void>	ret	= new Future<Void>();
//		ret.addResultListener(new DefaultResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("fin: "+plugin);
//			}
//		});
		
		if(plugins.get(plugin) == null)
		{
			initPlugin(plugin).addResultListener(new SwingDelegationResultListener<Void>(ret));
		}
		else
		{
			JComponent comp = plugin.getView();
			plugins.put(plugin, comp);
			if(props.getSubproperty(plugin.getName())!=null)
			{
				plugin.setProperties(props.getSubproperty(plugin.getName()))
					.addResultListener(new SwingDelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}

	protected IFuture<Void> initPlugin(final IControlCenterPlugin plugin)
	{
		final Future<Void> ret	= new Future<Void>();
		try
		{
			plugin.init(this).addResultListener(new SwingDelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					JComponent comp = plugin.getView();
					plugins.put(plugin, comp);
					if(props.getSubproperty(plugin.getName())!=null)
					{
						plugin.setProperties(props.getSubproperty(plugin.getName()))
							.addResultListener(new SwingDelegationResultListener<Void>(ret));
					}
					else
					{
						ret.setResult(null);
					}
				}
			});
			
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	//-------- IPropertiesProvider interface --------

	/**
	 *  Set state from given properties.
	 */
	public IFuture<Void> setProperties(final Properties props)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		final Future<Void>	ret	= new Future<Void>();
		
		this.props	= props;
		
		Properties	ccprops	= props.getSubproperty("controlcenter");
		if(ccprops==null)
		{
			// Use empty properties for initialization
			ccprops	= new Properties();
		}
		pccpanel.setProperties(ccprops).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				// Consider only settings of plugins, which have a panel associated.
				List	plugs	= new ArrayList();
				for(Iterator it=plugins.keySet().iterator(); it.hasNext();)
				{
					final IControlCenterPlugin	plugin	= (IControlCenterPlugin)it.next();
					if(plugins.get(plugin)!=null && props.getSubproperty(plugin.getName())!=null)
					{
						plugs.add(plugin);
					}
				}
				
				CounterResultListener	crl	= new CounterResultListener(plugs.size(),
					new SwingDelegationResultListener(ret));
				for(int i=0; i<plugs.size(); i++)
				{
					IControlCenterPlugin	plugin	= (IControlCenterPlugin)plugs.get(i);
					plugin.setProperties(props.getSubproperty(plugin.getName())).addResultListener(crl);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the current state as properties.
	 */
	public IFuture<Properties> getProperties()
	{
		if(!SwingUtilities.isEventDispatchThread())
			System.out.println("dreck");
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		final Future<Properties> ret	= new Future<Properties>();
		
		if(props==null)
		{
			ret.setException(new IllegalStateException("Properties not set."));
		}
		else
		{
//			System.out.println("Fetching panel properties.");
			pccpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					props.removeSubproperties("controlcenter");
					props.addSubproperties("controlcenter", (Properties)result);
					
					// Consider only settings of plugins, which have a panel associated.
					List	plugs	= new ArrayList();
					for(Iterator it=plugins.keySet().iterator(); it.hasNext();)
					{
						final IControlCenterPlugin	plugin	= (IControlCenterPlugin)it.next();
						if(plugins.get(plugin)!=null)
						{
							plugs.add(plugin);
						}
					}
					
//					System.out.println("Waiting for "+plugs.size()+" plugin properties.");
					final CounterResultListener	crl	= new CounterResultListener(plugs.size(),
						new SwingDelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
//							System.out.println("Fetched all plugin properties.");
							ret.setResult(props);
						}
					});
					for(int i=0; i<plugs.size(); i++)
					{
						final IControlCenterPlugin	plugin	= (IControlCenterPlugin)plugs.get(i);
//						System.out.println("Fetching plugin properties: "+plugin.getName());
						plugin.getProperties().addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								if(result!=null)
								{
									props.removeSubproperties(plugin.getName());
									props.addSubproperties(plugin.getName(), (Properties)result);
								}
//								System.out.println("Fetched plugin properties: "+plugin.getName());
								crl.resultAvailable(null);
							}
						});
					}
				}
			});
		}
		
		return ret;
	}
	
	//-------- IControlCenter interface --------

	/**
	 *  Add a new platform control center
	 *  or switch to tab if already exists.
	 */
	public void	showPlatform(IExternalAccess platformaccess)
	{
		controlcenter.showPlatform(platformaccess);
	}
	
	/**
	 *  Switch to a plugin.
	 *  Shows the plugin, if available.
	 */
	public void	showPlugin(String name)
	{
		IControlCenterPlugin	plugin	= null;
		for(Iterator<IControlCenterPlugin> it=plugins.keySet().iterator(); plugin==null && it.hasNext(); )
		{
			IControlCenterPlugin	next	= it.next();
			if(next.getName().equals(name))
			{
				plugin	= next;
			}
		}
		
		if(plugin!=null)
		{
			pccpanel.setPerspective(plugin);
		}
	}

	/**
	 * Set a text to be displayed in the status bar. The text will be removed
	 * automatically after some delay (or replaced by some other text).
	 */
	public void setStatusText(String text)
	{
		controlcenter.getWindow().getStatusBar().setText(text);
	}
	
	/**
	 *  Get a component from the status bar.
	 *  @param id	Id used for adding a component.
	 *  @return	The component to display.
	 */
	public JComponent	getStatusComponent(Object id)
	{
		return (JComponent)controlcenter.getWindow().getStatusBar().getStatusComponent(id);		
	}

	/**
	 * Add a component to the status bar.
	 * 
	 * @param id An id for later reference.
	 * @param comp An id for later reference.
	 */
	public void addStatusComponent(Object id, JComponent comp)
	{
		controlcenter.getWindow().getStatusBar().addStatusComponent(id, comp);
	}

	/**
	 * Remove a previously added component from the status bar.
	 * 
	 * @param id The id used for adding the component.
	 */
	public void removeStatusComponent(Object id)
	{
		controlcenter.getWindow().getStatusBar().removeStatusComponent(id);
	}

	/**
	 *  Display an error dialog.
	 * 
	 *  @param errortitle The title to use for an error dialog (required).
	 *  @param errormessage An optional error message displayed before the exception.
	 *  @param exception The exception (if any).
	 */
	public void displayError(String errortitle, String errormessage, Exception exception)
	{
		controlcenter.getWindow().displayError(errortitle, errormessage, exception);
	}
	
	/**
	 *  Get the platform access.
	 *  @return The external access.
	 */
	public IExternalAccess getPlatformAccess()
	{
		return platformaccess;
	}
	
	/**
	 *  Get the jcc access.
	 *  @return The external access.
	 */
	public IExternalAccess getJCCAccess()
	{
		return controlcenter.getJCCAccess();
	}
	
	/**
	 *  Get the cms update handler shared by all tools.
	 */
	public CMSUpdateHandler getCMSHandler()
	{
		return controlcenter.getCMSHandler();
	}

	/**
	 *  Get the component icon cache shared by all tools.
	 */
	public ComponentIconCache getIconCache()
	{
		return controlcenter.getIconCache();
	}
}
