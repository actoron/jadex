package jadex.tools.jcc;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.plugin.IControlCenter;
import jadex.base.gui.plugin.IControlCenterPlugin;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDelegationResultListener;

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
	protected Map	plugins;

	/** The global control center. */
	protected ControlCenter	controlcenter;

	/** The single platform control center panel. */
	protected PlatformControlCenterPanel	pccpanel;
	
	/** The settings of the control center and all plugins. */
	protected Properties	props;

	//-------- methods called by global control center --------

	/**
	 *  Initialize a control center.
	 */
	public IFuture	init(IExternalAccess platformaccess, ControlCenter controlcenter, final String[] plugin_classes)
	{
		this.platformaccess = platformaccess;
		this.controlcenter	= controlcenter;
		this.plugins = new LinkedHashMap();
		this.props	= new Properties();
		this.pccpanel	= new PlatformControlCenterPanel(this);
		
		// Load plugins.
		final Future	ret	= new Future();
		SServiceProvider.getService(controlcenter.getJCCAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)	throws Exception
			{
				ClassLoader cl = ((ILibraryService)result).getClassLoader();
				for(int i=0; i<plugin_classes.length; i++)
				{
					Class plugin_class = SReflect.classForName(plugin_classes[i], cl);
					IControlCenterPlugin p = (IControlCenterPlugin)plugin_class.newInstance();
					plugins.put(p, null);
					setStatusText("Plugin loaded successfully: "+ p.getName());
				}
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 * Close all active plugins. Called when the JCC exits.
	 */
	public void	dispose()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		// Close all plugins, which have a panel associated.
		for(Iterator it=plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
			if(plugins.get(plugin) != null)
			{
				try
				{
					plugin.shutdown();
				}
				catch(Exception e)
				{
					System.err.println("Exception while closing JCC-Plug-In " + plugin.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  Get the control center panel.
	 */
	public JComponent	getPanel()
	{
		return pccpanel;
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
		assert SwingUtilities.isEventDispatchThread();
		
		return (IControlCenterPlugin[])plugins.keySet().toArray(new IControlCenterPlugin[plugins.size()]);
	}
	
	/**
	 * Find a plugin by name.
	 * 
	 * @return null, when plugin is not found.
	 */
	public IControlCenterPlugin getPluginForName(String name)
	{
		assert SwingUtilities.isEventDispatchThread();
		
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
	public IFuture	activatePlugin(IControlCenterPlugin plugin)
	{
//		System.out.println("activate plugin: "+plugin);
		
		assert SwingUtilities.isEventDispatchThread();
		
		Future	ret	= new Future();
		
		if(plugins.get(plugin) == null)
		{
			try
			{
				plugin.init(this);
				JComponent comp = plugin.getView();
				plugins.put(plugin, comp);
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		if(!ret.isDone())
		{
			if(props.getSubproperty(plugin.getName())!=null)
			{
				plugin.setProperties(props.getSubproperty(plugin.getName()))
					.addResultListener(new SwingDelegationResultListener(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	//-------- IPropertiesProvider interface --------

	/**
	 *  Set state from given properties.
	 */
	public IFuture setProperties(final Properties props)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final Future	ret	= new Future();
		
		this.props	= props;
		
		Properties	ccprops	= props.getSubproperty("controlcenter");
		if(ccprops==null)
		{
			// Use empty properties for initialization
			ccprops	= new Properties();
		}
		pccpanel.setProperties(ccprops).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
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
	public IFuture getProperties()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final Future	ret	= new Future();
		
		if(props==null)
		{
			ret.setException(new IllegalStateException("Properties not set."));
		}
		else
		{
			pccpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
			{
				public void customResultAvailable(Object result) throws Exception
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
					
					final CounterResultListener	crl	= new CounterResultListener(plugs.size(),
						new SwingDelegationResultListener(ret)
					{
						public void customResultAvailable(Object result) throws Exception
						{
							ret.setResult(props);
						}
					});
					for(int i=0; i<plugs.size(); i++)
					{
						final IControlCenterPlugin	plugin	= (IControlCenterPlugin)plugs.get(i);
						plugin.getProperties().addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)	throws Exception
							{
								if(result!=null)
								{
									props.removeSubproperties(plugin.getName());
									props.addSubproperties(plugin.getName(), (Properties)result);
								}
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
	 * Set a text to be displayed in the status bar. The text will be removed
	 * automatically after some delay (or replaced by some other text).
	 */
	public void setStatusText(String text)
	{
		controlcenter.getWindow().getStatusBar().setText(text);
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
}
