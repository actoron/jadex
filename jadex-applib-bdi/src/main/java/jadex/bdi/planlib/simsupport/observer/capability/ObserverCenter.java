package jadex.bdi.planlib.simsupport.observer.capability;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.IViewportListener;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.IExternalEngineAccess;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.IObserverCenterPlugin;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.ObjectIntrospectorPlugin;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.VisualsPlugin;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.ILibraryService;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

/** The default observer center
 */
public class ObserverCenter
{
	private static final int[] PLUGIN_REFRESH_TIMES = { 0, 50, 100, 250, 500, 1000};
	
	private static final int[] VIEWPORT_RATES = { 5, 10, 20, 30, 50, 60, 70, 75, 85, 100, 120 };
	
	/** The observer agent
	 */
	private IExternalAccess agent_;
	
	/** The main window.
	 */
	private ObserverCenterWindow mainWindow_;
	
	/** Currently active plugin
	 */
	private IObserverCenterPlugin activePlugin_;
	
	/** The plugins
	 */
	private IObserverCenterPlugin[] plugins_;
	
	/** Plugin refresh timer
	 */
	private Timer timer_;
	
	/** Selection controller
	 */
	private SelectionController selectionController_;
	
	/** Creates an observer center using the simulation viewport
	 *  
	 *  @param agent the observer agent
	 */
	public ObserverCenter(IExternalAccess agent)
	{
		agent_ = agent;
		activePlugin_ = null;
		selectionController_ = new SelectionController();
		EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					String mainTitle = (String) agent_.getBeliefbase().getBelief("environment_name").getFact();
					mainWindow_ = new ObserverCenterWindow(mainTitle, getViewport().getCanvas());
					loadPlugins();
					
					JMenu refreshMenu = new JMenu("Display");
					
					JMenu pluginMenu = new JMenu("Plugin Refresh");
					ButtonGroup group = new ButtonGroup();
					
					for (int i = 0; i < PLUGIN_REFRESH_TIMES.length; ++i)
					{
						JRadioButtonMenuItem item = new JRadioButtonMenuItem(new PluginRefreshAction(PLUGIN_REFRESH_TIMES[i]));
						if (PLUGIN_REFRESH_TIMES[i] == 100)
						{
							item.setSelected(true);
						}
						group.add(item);
						pluginMenu.add(item);
					}
					refreshMenu.add(pluginMenu);
					
					JMenu viewportMenu = new JMenu("Viewport Refresh");
					group = new ButtonGroup();
					
					for (int i = 0; i < VIEWPORT_RATES.length; ++i)
					{
						JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ViewportRefreshAction(VIEWPORT_RATES[i]));
						if (VIEWPORT_RATES[i] == 30)
						{
							item.setSelected(true);
						}
						group.add(item);
						viewportMenu.add(item);
					}
					refreshMenu.add(viewportMenu);
					
					mainWindow_.addMenu(refreshMenu);
					
					timer_ = new Timer(100, new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								synchronized(plugins_)
								{
									if (activePlugin_ != null)
									{
										activePlugin_.refresh();
									}
								}
							}
						});
					timer_.start();
					
					mainWindow_.addWindowListener(new ObserverWindowController());
					
					setEnableSelection(true);
				}
			});
	}
	
	/** Returns access to the agent
	 * 
	 *  @return observer agent access
	 */
	public IExternalAccess getAgentAccess()
	{
		return agent_;
	}
	
	/** Returns access to the simulation engine
	 * 
	 *  @return simulation engine access
	 */
	public IExternalEngineAccess getEngineAccess()
	{
		return (IExternalEngineAccess) agent_.getBeliefbase().getBelief("simulation_engine_access").getFact();
	}
	
	/** Returns the simulation viewport
	 * 
	 *  @return simulation viewport
	 */
	public IViewport getViewport()
	{
		return (IViewport) agent_.getBeliefbase().getBelief("viewport").getFact();
	}
	
	/** Marks an object.
	 *  
	 *  @param object to mark, null for deselection
	 */
	public void markObject(final Integer objectId)
	{
		agent_.invokeLater(new Runnable()
		{
			public void run()
			{
				agent_.getBeliefbase().getBelief("marked_object").setFact(objectId);
				EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							synchronized(plugins_)
							{
								if (activePlugin_ != null)
								{
									activePlugin_.refresh();
								}
							}
						}
					});
			}
		});
	}
	
	/** Returns the currently marked object.
	 * 
	 *  @return currently marked object
	 */
	public Integer getMarkedObject()
	{
		return (Integer) agent_.getBeliefbase().getBelief("marked_object").getFact();
	}
	
	/** Enables and disables selection.
	 *  
	 *  @param enabled true to enable selection
	 */
	public void setEnableSelection(boolean enabled)
	{
		if (enabled)
		{
			getViewport().addViewportListener(selectionController_);
		}
		else
		{
			getViewport().removeViewportListener(selectionController_);
		}
	}
	
	/** Loads all available plugins
	 */
	private void loadPlugins()
	{
		ArrayList plugins = new ArrayList();
		
		IObserverCenterPlugin plugin = new ObjectIntrospectorPlugin();
		// default plugins
		// TODO: remove hard coding
		plugins.add(plugin);
		plugin = new VisualsPlugin();
		plugins.add(plugin);
		//plugin = new ToolboxPlugin();
		//plugins.add(plugin);
		
		// custom plugins
		IBeliefSet customPluginBelSet = agent_.getBeliefbase().getBeliefSet("custom_plugins");
		if (customPluginBelSet.size() > 0)
		{
			Object[] customPlugins = agent_.getBeliefbase().getBeliefSet("custom_plugins").getFacts();
			plugins.addAll(Arrays.asList(customPlugins));
		}
		
		plugins_ = (IObserverCenterPlugin[]) plugins.toArray(new IObserverCenterPlugin[0]);
		
		for (int i = 0; i < plugins_.length; ++i)
		{
			addPluginButton(plugins_[i]);
		}
		
		activatePlugin(plugins_[0]);
	}
	
	/** Adds a plugin to the toolbar.
	 * 
	 *  @param plugin the plugin
	 */
	private void addPluginButton(IObserverCenterPlugin plugin)
	{
		String iconPath = plugin.getIconPath();
		if (iconPath == null)
		{
			mainWindow_.addToolbarItem(plugin.getName(), new PluginAction(plugin));
		}
		else
		{
			ClassLoader cl = ((ILibraryService) agent_.getBeliefbase().getBelief("library_service").getFact()).getClassLoader();
			try
			{
				System.out.println(iconPath);
				System.out.println(cl.getResource(iconPath));
				BufferedImage image = ImageIO.read(cl.getResource(iconPath));
				ImageIcon icon = new ImageIcon(image);
				mainWindow_.addToolbarItem(plugin.getName(), icon, new PluginAction(plugin));
			}
			catch (Exception e)
			{
				System.err.println("Icon image " + iconPath + " not found.");
				mainWindow_.addToolbarItem(plugin.getName(), new PluginAction(plugin));
			}
		}
	}
	
	private void activatePlugin(IObserverCenterPlugin plugin)
	{
		synchronized (plugins_)
		{
			IObserverCenterPlugin oldPlugin = activePlugin_;
			if (oldPlugin != null)
			{
				oldPlugin.shutdown();
			}

			mainWindow_.setPluginView(plugin.getView());
			plugin.start(this);
			activePlugin_ = plugin;
		}
	}
	
	private class PluginAction extends AbstractAction
	{
		private IObserverCenterPlugin plugin_;
		
		public PluginAction(IObserverCenterPlugin plugin)
		{
			plugin_ = plugin;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			activatePlugin(plugin_);
		}
	}
	
	private class PluginRefreshAction extends AbstractAction
	{
		private int time_;
		
		/** Creates new PluginRefreshAction
		 * 
		 * @param time Time delay to set
		 */
		public PluginRefreshAction(int time)
		{
			time_ = time;
			if (time_ <= 0)
			{
				putValue(NAME, "Never");
			}
			else
			{
				putValue(NAME, Integer.toString(time_) + "ms");
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			timer_.stop();
			if (time_ > 0)
			{
				timer_.setDelay(time_);
				timer_.start();
			}
		}
	}
	
	private class ViewportRefreshAction extends AbstractAction
	{
		private int fps_;
		
		/** Creates new PluginRefreshAction
		 * 
		 * @param fps frames per second
		 */
		public ViewportRefreshAction(int fps)
		{
			fps_ = fps;
			if (fps_ < 0)
			{
				fps_ = 0;
			}
			if (fps_ == 0)
			{
				putValue(NAME, "Unlimited");
			}
			else
			{
				putValue(NAME, Integer.toString(fps_) + " FPS");
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			agent_.getBeliefbase().getBelief("frame_rate").setFact(new Integer(fps_));
		}
	}
	
	private class SelectionController implements IViewportListener
	{
		public void leftClicked(IVector2 position)
		{
			IVector1 maxDist = (IVector1) getAgentAccess().getBeliefbase().getBelief("selector_distance").getFact();
			final Integer observedId = getEngineAccess().getNearestObjectId(position, maxDist);
			
			markObject(observedId);
		}
		
		public void rightClicked(IVector2 position)
		{
		}
	}
	
	private class ObserverWindowController implements WindowListener
	{
		public void windowActivated(WindowEvent e)
		{
		}
		
		public void windowClosed(WindowEvent e)
		{
		}
		
		public void windowClosing(WindowEvent e)
		{
			timer_.stop();
			mainWindow_.dispose();
			agent_.invokeLater(new Runnable()
				{
					public void run()
					{
						agent_.killAgent();
						
					}
				});
		}
		
		public void windowDeactivated(WindowEvent e)
		{
		}
		
		public void windowDeiconified(WindowEvent e)
		{
		}
		
		public void windowIconified(WindowEvent e)
		{
		}
		
		public void windowOpened(WindowEvent e)
		{
		}
		
	}
}
