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
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

import java.util.ArrayList;
import java.util.HashMap;
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
//	protected Map<IControlCenterPlugin, JComponent>	plugins;
	protected List<Tuple2<IControlCenterPlugin, JComponent>> plugins;
	
	/** The plugin toolbar visibility. */
	protected Map<IControlCenterPlugin, Boolean> toolbarvis;
	
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
//		this.plugins = new LinkedHashMap<IControlCenterPlugin, JComponent>();
		this.plugins = new ArrayList<Tuple2<IControlCenterPlugin, JComponent>>();
		this.toolbarvis = new HashMap<IControlCenterPlugin, Boolean>();
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
//								plugins.put(p, null);
								addPluginComponent(p, null);
								
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
	 * 
	 */
	protected JComponent getPluginComponent(IControlCenterPlugin pl)
	{
		JComponent ret = null;
		
		for(Tuple2<IControlCenterPlugin, JComponent> tup: plugins)
		{
			if(tup.getFirstEntity().equals(pl))
			{
				ret = tup.getSecondEntity();
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected Tuple2<IControlCenterPlugin, JComponent> getPluginTuple(IControlCenterPlugin pl)
	{
		Tuple2<IControlCenterPlugin, JComponent> ret = null;
		
		for(Tuple2<IControlCenterPlugin, JComponent> tup: plugins)
		{
			if(tup.getFirstEntity().equals(pl))
			{
				ret = tup;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void addPluginComponent(IControlCenterPlugin pl, JComponent comp)
	{
		// Remove old
		int pos = -1;
		for(int i=0; i<plugins.size(); i++)
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = plugins.get(i);
			if(tup.getFirstEntity().equals(pl))
			{
				plugins.remove(tup);
				pos = i;
				break;
			}
		}
		Tuple2<IControlCenterPlugin, JComponent> tup = new Tuple2<IControlCenterPlugin, JComponent>(pl, comp);
		// Keep old position
		if(pos!=-1)
		{
			plugins.add(pos, tup);
		}
		else
		{
			plugins.add(tup);
		}
	}
	
	/**
	 * 
	 */
	protected void removePluginComponent(IControlCenterPlugin pl)
	{
		// Remove old
		for(int i=0; i<plugins.size(); i++)
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = plugins.get(i);
			if(tup.getFirstEntity().equals(pl))
			{
				plugins.remove(tup);
				break;
			}
		}
	}
	
	/**
	 * 
	 */
	protected void moveLeftPlugin(IControlCenterPlugin pl)
	{
		// Remove old
		int pos = -1;
		IControlCenterPlugin[] pls = getToolbarPlugins(true);
		Tuple2<IControlCenterPlugin, JComponent> last = null;
		for(int i=0; i<pls.length; i++)
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = getPluginTuple(pls[i]);
			
			if(tup.getFirstEntity().equals(pl))
			{
				if(last!=null)
				{
					int idx = getPluginIndex(last.getFirstEntity());
					plugins.remove(tup);
					plugins.add(idx, tup);
				}
				break;
			}
			
			last = tup;
		}
	}
	
	/**
	 * 
	 */
	protected void moveRightPlugin(IControlCenterPlugin pl)
	{
		// Remove old
		int pos = -1;
		IControlCenterPlugin[] pls = getToolbarPlugins(true);
		Tuple2<IControlCenterPlugin, JComponent> last = null;
		for(int i=0; i<pls.length; i++)
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = getPluginTuple(pls[i]);
			
			if(tup.getFirstEntity().equals(pl))
			{
				if(i+1<pls.length)
				{
					int idx = getPluginIndex(pls[i+1]);
					plugins.remove(tup);
					plugins.add(idx, tup);
					break;
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected int getPluginIndex(IControlCenterPlugin pl)
	{
		int ret = -1;
		
		for(int i=0; i<plugins.size(); i++)
		{
			if(plugins.get(i).getFirstEntity().equals(pl))
			{
				ret = i;
				break;
			}
		}
		
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
		for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it=plugins.iterator(); it.hasNext();)
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = it.next();
			IControlCenterPlugin plugin = (IControlCenterPlugin)tup.getFirstEntity();
			if(tup.getSecondEntity() != null)
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
				if(getPluginComponent(aplugins[i])!=null)
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
	 *  Test if a plugin is visible in the toolbar.
	 *  @param pl The plugin.
	 *  @return True, if is visible.
	 */
	public boolean isPluginVisible(IControlCenterPlugin pl)
	{
		Boolean ret = toolbarvis.get(pl);
		return ret!=null? ret.booleanValue(): true;
	}
	
	/**
	 *  Set the visible state of a plugin.
	 */
	public void setPluginVisible(IControlCenterPlugin pl, boolean vis)
	{
		toolbarvis.put(pl, vis);
	}
	
	/**
	 *  Get the toolbar plugins that are visible or not visible.
	 */
	public IControlCenterPlugin[] getToolbarPlugins(boolean vis)
	{
		List<IControlCenterPlugin> ret = new ArrayList<IControlCenterPlugin>();
		for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it = plugins.iterator(); it.hasNext(); )
		{
			Tuple2<IControlCenterPlugin, JComponent> tup = it.next();
			if(isPluginVisible(tup.getFirstEntity())==vis)
			{
				ret.add(tup.getFirstEntity());
			}
		}
		return ret.toArray(new IControlCenterPlugin[ret.size()]);
	}
	
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

		IControlCenterPlugin[] ret = new IControlCenterPlugin[plugins.size()];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = plugins.get(i).getFirstEntity();
		}
	
		return ret;
//		return (IControlCenterPlugin[])plugins.keySet().toArray(new IControlCenterPlugin[plugins.size()]);
	}
	
	/**
	 * Find a plugin by name.
	 * 
	 * @return null, when plugin is not found.
	 */
	public IControlCenterPlugin getPluginForName(String name)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
	
		if(name==null)
			throw new IllegalArgumentException("Name must not null.");
		
		for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it=plugins.iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next().getFirstEntity();
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
		
		if(getPluginComponent(plugin) == null)
		{
			initPlugin(plugin).addResultListener(new SwingDelegationResultListener<Void>(ret));
		}
		else
		{
			JComponent comp = plugin.getView();
//			plugins.put(plugin, comp);
			addPluginComponent(plugin, comp);
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
//					plugins.put(plugin, comp);
					addPluginComponent(plugin, comp);
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
		
		Properties[] vis = props.getSubproperties("vis");
		if(vis!=null && vis.length>0)
		{
			Property[] ps = vis[0].getProperties();
			if(ps!=null)
			{
				for(int i=0; i<ps.length; i++)
				{
					IControlCenterPlugin plg = getPluginForName(ps[i].getType());
					if(plg!=null)
					{
//						System.out.println("vis: "+ps[i].getType()+" "+ps[i].getValue());
						toolbarvis.put(plg, Boolean.valueOf(ps[i].getValue()).booleanValue());
					}
				}
			}
		}
			
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
				List<IControlCenterPlugin>	plugs	= new ArrayList();
				for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it=plugins.iterator(); it.hasNext();)
				{
					final IControlCenterPlugin	plugin	= it.next().getFirstEntity();
					if(getPluginComponent(plugin)!=null && props.getSubproperty(plugin.getName())!=null)
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
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		final Future<Properties> ret	= new Future<Properties>();
		
		if(props==null)
		{
			ret.setException(new IllegalStateException("Properties not set."));
		}
		else
		{
			Properties vis = new Properties();
			for(Iterator<IControlCenterPlugin> it = toolbarvis.keySet().iterator(); it.hasNext(); )
			{
				IControlCenterPlugin plg = it.next();
//				System.out.println("vis save: "+plg.getName()+" "+toolbarvis.get(plg));
				vis.addProperty(new Property(plg.getName(), toolbarvis.get(plg).toString()));
			}
			props.removeSubproperties("vis");
			props.addSubproperties("vis", vis);
			
//			System.out.println("Fetching panel properties.");
			pccpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					props.removeSubproperties("controlcenter");
					props.addSubproperties("controlcenter", (Properties)result);
					
					// Consider only settings of plugins, which have a panel associated.
					List	plugs	= new ArrayList();
					for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it=plugins.iterator(); it.hasNext();)
					{
						Tuple2<IControlCenterPlugin, JComponent> tup = it.next();
						final IControlCenterPlugin	plugin	= tup.getFirstEntity();
						if(tup.getSecondEntity()!=null)
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
		for(Iterator<Tuple2<IControlCenterPlugin, JComponent>> it=plugins.iterator(); plugin==null && it.hasNext(); )
		{
			IControlCenterPlugin	next	= it.next().getFirstEntity();
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
