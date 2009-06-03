package jadex.adapter.base.envsupport.observer.gui;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.gui.plugin.IObserverCenterPlugin;
import jadex.adapter.base.envsupport.observer.gui.plugin.ObjectIntrospectorPlugin;
import jadex.adapter.base.envsupport.observer.gui.plugin.VisualsPlugin;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;
import jadex.bridge.ILibraryService;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private ObserverCenterWindow mainwindow;
	
	/** Currently active plugin
	 */
	private IObserverCenterPlugin activeplugin;
	
	/** The plugins
	 */
	private IObserverCenterPlugin[] plugins;
	
	/** Viewport refresh timer
	 */
	private Timer vptimer;
	
	/** Plugin refresh timer
	 */
	private Timer plugintimer;
	
	/** The library service */
	private ILibraryService libService;
	
	/** Additional IDataView objects */
	private Map externaldataviews;
	
	/** Selected dataview name */
	private String selecteddataviewname;
	
	/** Perspectives */
	private Map perspectives;
	
	/** Selected Perspective */
	private IPerspective selectedperspective;
	
	/** The current space */
	private Space2D space;
	
	//TODO: move to Perspective!
	/** Area size of the space */
	private IVector2 areasize;
	
	/** Creates an observer center.
	 *  
	 *  @param windowTitle title of the observer window
	 *  @param space the space being observed
	 *  @param libSrvc the platform library service for loading resources (images etc.)
	 *  @param customplugins custom plugins used in the observer
	 */
	public ObserverCenter(final String windowTitle, final IEnvironmentSpace space, ILibraryService libSrvc, List customplugins)
	{
		this.space = (Space2D) space;
		areasize = ((Space2D)space).getAreaSize().copy();
		perspectives = Collections.synchronizedMap(new HashMap());
		externaldataviews = Collections.synchronizedMap(new HashMap());
		final List cPlugins = customplugins == null? new ArrayList(): customplugins;
		this.libService = libSrvc;
		Map spaceviews = space.getDataViews();
		if(!spaceviews.isEmpty())
			selecteddataviewname = (String) spaceviews.keySet().iterator().next();
		activeplugin = null;
		
		Runnable init = new Runnable()
		{
			public void run()
			{
				mainwindow = new ObserverCenterWindow(windowTitle);
				loadPlugins(cPlugins);
				
				JMenu refreshMenu = new JMenu("Display");
				
				JMenu pluginMenu = new JMenu("Plugin Refresh");
				ButtonGroup group = new ButtonGroup();
				
				for(int i = 0; i < PLUGIN_REFRESH_TIMES.length; ++i)
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
				
				for(int i = 0; i < VIEWPORT_RATES.length; ++i)
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
				
				mainwindow.addMenu(refreshMenu);
				
				plugintimer = new Timer(100, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						synchronized(ObserverCenter.this.plugins)
						{
							if(activeplugin != null)
							{
								activeplugin.refresh();
							}
						}
					}
				});
				plugintimer.start();
				
				vptimer = new Timer(33, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						updateDisplay();
					}
				});
				vptimer.start();
				
				mainwindow.addWindowListener(new ObserverWindowController());
			}
		};
		
		if(EventQueue.isDispatchThread())
		{
			init.run();
		}
		else
		{
			try
			{
				EventQueue.invokeAndWait(init);
			}
			catch (InterruptedException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
	}
	
	//TODO:Move to Perspective!
	/**
	 * Returns the area size.
	 * 
	 * @return area size
	 */
	public IVector2 getAreaSize()
	{
		return areasize;
	}
	
	/**
	 * Adds an additional dataview.
	 * @param name name of the dataview
	 * @param dataview an additional dataview
	 */
	public void addDataView(String name, IDataView dataview)
	{
		synchronized (dataview)
		{
			externaldataviews.put(name, dataview);
			if (selecteddataviewname == null)
			{
				selecteddataviewname = name;
			}
		}
	}
	
	/**
	 * Returns the available dataviews.
	 * 
	 *  @return the available dataviews
	 */
	public Map getDataViews()
	{
		Map allViews = new HashMap();
		allViews.putAll(externaldataviews);
		allViews.putAll(space.getDataViews());
		return allViews;
	}
	
	
	/**
	 * Returns the selected dataview.
	 * 
	 *  @return the selected dataview
	 */
	public IDataView getSelectedDataView()
	{
		IDataView dataview = space.getDataView(selecteddataviewname);
		if (dataview == null)
			dataview = (IDataView) externaldataviews.get(selecteddataviewname);
		return dataview;
	}
	
	/**
	 * Returns the selected dataview name.
	 * 
	 *  @return the selected dataview name
	 */
	public String getSelectedDataViewName()
	{
		return selecteddataviewname;
	}
	
	/**
	 * Sets the selected dataview.
	 * 
	 *  @param name name of the dataview to be selected
	 */
	public void setSelectedDataView(String name)
	{
		selecteddataviewname = name;
	}
	
	/**
	 * Adds a perspective.
	 * @param name name of the perspective
	 * @param perspective the perspective
	 */
	public void addPerspective(String name, IPerspective perspective)
	{
		synchronized(perspective)
		{
			perspective.setObserverCenter(this);
			perspective.setName(name);
			perspectives.put(name, perspective);
			if (perspectives.size() == 1)
			{
				setSelectedPerspective(name);
			}
		}
	}
	
	/**
	 * Returns access to the library service
	 * 
	 *  @return the library service
	 */
	public ILibraryService getLibraryService()
	{
		return libService;
	}
	
	/**
	 * Returns the available perspectives.
	 * 
	 *  @return the available perspectives
	 */
	public Map getPerspectives()
	{
		return perspectives;
	}
	
	
	/**
	 * Returns the selected perspective.
	 * 
	 *  @return the selected perspective
	 */
	public IPerspective getSelectedPerspective()
	{
		return selectedperspective;
	}
	
	/**
	 * Sets the selected perspective.
	 * 
	 *  @param name name of the perspective
	 */
	public void setSelectedPerspective(String name)
	{
		synchronized(perspectives)
		{
			IPerspective perspective = (IPerspective) perspectives.get(name);
			perspective.setObserverCenter(this);
			selectedperspective = perspective;
			mainwindow.setPerspectiveView(perspective.getView());
		}
	}
	
	/**
	 * Returns the space.
	 * @return the space
	 */
	public Space2D getSpace()
	{
		return space;
	}
	
	/**
	 * Loads all available plugins
	 * @param customplugins custom plugins used in addition to standard plugins
	 */
	private void loadPlugins(List customplugins)
	{
		ArrayList plugins = new ArrayList();
		
		IObserverCenterPlugin plugin = new ObjectIntrospectorPlugin();
		// default plugins
		// TODO: remove hard coding
		plugins.add(plugin);
		// TODO: port from simsupport
		plugin = new VisualsPlugin();
		plugins.add(plugin);
		//plugin = new ToolboxPlugin();
		//plugins.add(plugin);
		plugins.addAll(customplugins);
		
		this.plugins = (IObserverCenterPlugin[]) plugins.toArray(new IObserverCenterPlugin[0]);
		
		for (int i = 0; i < this.plugins.length; ++i)
		{
			addPluginButton(this.plugins[i]);
		}
		
		if(this.plugins.length>0)
			activatePlugin(this.plugins[0]);
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
			mainwindow.addToolbarItem(plugin.getName(), new PluginAction(plugin));
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
				mainwindow.addToolbarItem(plugin.getName(), icon, new PluginAction(plugin));
			}
			catch (Exception e)
			{
				System.err.println("Icon image " + iconPath + " not found.");
				mainwindow.addToolbarItem(plugin.getName(), new PluginAction(plugin));
			}
		}
	}
	
	private void activatePlugin(IObserverCenterPlugin plugin)
	{
		synchronized (this.plugins)
		{
			IObserverCenterPlugin oldPlugin = activeplugin;
			if (oldPlugin != null)
			{
				oldPlugin.shutdown();
			}

			mainwindow.setPluginView(plugin.getView());
			plugin.start(this);
			activeplugin = plugin;
		}
	}
	
	/**
	 * Updates the display.
	 */
	private void updateDisplay()
	{
		synchronized(perspectives)
		{
			if(selectedperspective != null)
			{
				selectedperspective.refresh();
			}
		}
	}
	
	private class PluginAction extends AbstractAction
	{
		private IObserverCenterPlugin plugin;
		
		public PluginAction(IObserverCenterPlugin plugin)
		{
			this.plugin = plugin;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			activatePlugin(this.plugin);
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
			plugintimer.stop();
			if (time_ > 0)
			{
				plugintimer.setDelay(time_);
				plugintimer.start();
			}
		}
	}
	
	private class ViewportRefreshAction extends AbstractAction
	{
		private int delay;
		
		/** Creates new PluginRefreshAction
		 * 
		 * @param fps frames per second
		 */
		public ViewportRefreshAction(int fps)
		{
			delay = 1000 / fps;
			if (fps < 0)
			{
				fps = 0;
			}
			if (fps == 0)
			{
				putValue(NAME, "Unlimited");
			}
			else
			{
				putValue(NAME, Integer.toString(fps) + " FPS");
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			vptimer.stop();
			vptimer.setDelay(delay);
			vptimer.start();
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
			dispose();
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
	
	/**
	 *  Dispose the observer center. 
	 */
	public void dispose()
	{
		plugintimer.stop();
		mainwindow.dispose();
	}
}
