package jadex.bdi.planlib.simsupport.observer.capability;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.IViewportListener;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.IExternalEngineAccess;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.IObserverCenterPlugin;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.ObjectIntrospectorPlugin;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.VisualsPlugin;
import jadex.bdi.runtime.IExternalAccess;

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
	
	/** Creates an observer center using the simulation viewport
	 *  
	 *  @param agent the observer agent
	 */
	public ObserverCenter(IExternalAccess agent)
	{
		agent_ = agent;
		activePlugin_ = null;
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
	 *  @param object to mark
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
	
	/** Loads all available plugins
	 */
	private void loadPlugins()
	{
		ArrayList plugins = new ArrayList();
		
		// TODO: remove hard coding
		IObserverCenterPlugin plugin = new ObjectIntrospectorPlugin();
		plugins.add(plugin);
		plugin = new VisualsPlugin();
		plugins.add(plugin);
		
		plugins_ = (IObserverCenterPlugin[]) plugins.toArray(new IObserverCenterPlugin[0]);
		
		for (int i = 0; i < plugins_.length; ++i)
		{
			mainWindow_.addToolbarItem(plugins_[i].getName(), new PluginAction(plugins_[i]));
		}
		
		activatePlugin(plugins_[0]);
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
