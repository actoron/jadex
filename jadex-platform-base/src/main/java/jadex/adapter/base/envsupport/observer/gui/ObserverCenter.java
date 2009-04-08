package jadex.adapter.base.envsupport.observer.gui;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.view.GeneralView2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.IViewport;
import jadex.adapter.base.envsupport.observer.graphics.IViewportListener;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.gui.plugin.IObserverCenterPlugin;
import jadex.adapter.base.envsupport.observer.gui.plugin.ObjectIntrospectorPlugin;
import jadex.bridge.ILibraryService;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	/** The library service */
	private ILibraryService libService;
	
	/** The space that is being observed. */
	private IEnvironmentSpace space;
	
	/** The configuration of the observer */
	private Configuration config;
	
	/** Currently marked object */
	private Object markedObject;
	
	/** Currently selected theme */
	private String selectedTheme;
	
	/** Creates an observer center.
	 *  
	 *  @param space the space being observed
	 *  @param cfg the configuration
	 *  @param libSrvc the platform library service for loading resources (images etc.)
	 */
	public ObserverCenter(final IEnvironmentSpace space, Configuration cfg, final ILibraryService libSrvc)
	{
		this.libService = libSrvc;
		this.space = space;
		this.config = cfg;
		selectedTheme = config.getThemeNames()[0];
		activePlugin_ = null;
		selectionController_ = new SelectionController();
		EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					String mainTitle = config.getWindowTitle();
					mainWindow_ = new ObserverCenterWindow(mainTitle, libService, config.useOpenGl());
					IViewport vp = mainWindow_.getViewport();
					vp.setSize(((Space2D) space).getAreaSize());
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
					
					String[] themeNames = config.getThemeNames();
					for (int i = 0; i < themeNames.length; ++i)
					{
						Map theme = config.getTheme(themeNames[i]);
						for (Iterator it = theme.values().iterator(); it.hasNext(); )
						{
							Object item = it.next();
							if (item instanceof DrawableCombiner)
							{
								DrawableCombiner d = (DrawableCombiner) item;
								getViewport().registerDrawableCombiner(d);
							}
						}
					}
					
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
								updateDisplay();
							}
						});
					timer_.start();
					
					mainWindow_.addWindowListener(new ObserverWindowController());
					
					setEnableSelection(true);
				}
			});
	}
	
	/** Returns the configuration
	 * 
	 *  @return observer configuration
	 */
	public Configuration getConfig()
	{
		return config;
	}
	
	/** Returns access to the environment space
	 * 
	 *  @return simulation engine access
	 */
	public IEnvironmentSpace getEnvironmentSpace()
	{
		return space;
	}
	
	/** Returns the simulation viewport
	 * 
	 *  @return simulation viewport
	 */
	public IViewport getViewport()
	{
		return mainWindow_.getViewport();
	}
	
	/** Marks an object.
	 *  
	 *  @param object to mark, null for deselection
	 */
	public void markObject(final Object objectId)
	{
		markedObject = objectId;
		if (activePlugin_ != null)
		{
			activePlugin_.refresh();
		}
	}
	
	/** Returns the currently marked object.
	 * 
	 *  @return currently marked object
	 */
	public Object getMarkedObject()
	{
		return markedObject;
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
		// TODO: port from simsupport
		//plugin = new VisualsPlugin();
		//plugins.add(plugin);
		//plugin = new ToolboxPlugin();
		//plugins.add(plugin);
		
		plugins_ = config.getPlugins();
		
		for (int i = 0; i < plugins_.length; ++i)
		{
			addPluginButton(plugins_[i]);
		}
		
		if(plugins_.length>0)
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
			ClassLoader cl = libService.getClassLoader();
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
	
	/**
	 * Updates the display.
	 */
	private void updateDisplay()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				IViewport viewport = getViewport();
				viewport.setInvertX(config.getInvertXAxis());
				viewport.setInvertY(config.getInvertYAxis());
				viewport.setObjectShift(config.getObjectShift());
				
				// Set pre- and postlayers
				Map theme = config.getTheme(selectedTheme);
				List preLayers = (List) theme.get("prelayers");
				List postLayers = (List) theme.get("postlayers");
				viewport.setPreLayers(preLayers);
				viewport.setPostLayers(postLayers);
				
				Object[] objects = space.getView(GeneralView2D.class.getName()).getObjects();
				List objectList = null;
				objectList = new ArrayList(objects.length + 1);
				for (int i = 0; i < objects.length; ++i )
				{
					ISpaceObject obj = (ISpaceObject) objects[i];
					DrawableCombiner d = (DrawableCombiner) theme.get(obj.getType());
					Object[] viewObj = new Object[3];
					viewObj[0] = ((IVector2) obj.getProperty("position")).copy();
					IVector2 vel = ((IVector2) obj.getProperty("velocity"));
					if (vel != null)
					{
						viewObj[1] = vel.copy();
					}
					viewObj[2] = d;
					objectList.add(viewObj);
				}
				
				ISpaceObject mObj = null;
				if (markedObject != null)
				{
					mObj = (ISpaceObject) space.getSpaceObject(markedObject);
				}
				if (mObj != null)
				{
					IVector2 size = ((DrawableCombiner) theme.get(mObj.getType())).getSize();
					size.multiply(2.0);
					Object[] viewObj = new Object[3];
					DrawableCombiner marker = (DrawableCombiner) theme.get("marker");
					marker.setDrawableSizes(size);
					viewObj[0] = mObj.getProperty("position");
					viewObj[2] = marker;
					objectList.add(viewObj);
				}
				else
				{
					markedObject = null;
				}
				
				Comparator drawOrder = config.getDisplayOrder();
				if (drawOrder != null)
				{
					Collections.sort(objectList, drawOrder);
				}
				
				viewport.setObjectList(objectList);
				viewport.refresh();
			}
		});
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
			config.setFps(fps_);
		}
	}
	
	private class SelectionController implements IViewportListener
	{
		public void leftClicked(IVector2 position)
		{
			IVector1 maxDist = config.getSelectorDistance();
			((Space2D) space).getNearestObject(position, maxDist);
			ISpaceObject obj = ((Space2D) space).getNearestObject(position, maxDist);
			final Object observedId = obj.getId();
			
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
