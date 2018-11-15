package jadex.extension.envsupport.observer.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.IChangeListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.gui.plugin.IObserverCenterPlugin;
import jadex.extension.envsupport.observer.gui.plugin.IntrospectorPlugin;
import jadex.extension.envsupport.observer.gui.plugin.VisualsPlugin;
import jadex.extension.envsupport.observer.perspective.IPerspective;
import jadex.extension.envsupport.observer.perspective.Perspective2D;
import jadex.extension.envsupport.observer.perspective.Perspective3D;

/** 
 *  The default observer center.
 */
public class ObserverCenter implements IObserverCenter
{
	private static final int[] PLUGIN_REFRESH_TIMES = { 0, 50, 100, 250, 500, 1000};
	
	private static final int[] VIEWPORT_RATES = { -1, 0, 5, 10, 20, 30, 50, 60, 70, 75, 85, 100, 120 };
	private static final int DEFAULT_VIEWPORT_RATE = -1;
	
	/** The main window. */
	private ObserverCenterWindow mainwindow;
	
	/** Currently active plugin. */
	private IObserverCenterPlugin activeplugin;
	
	/** The plugins. */
	private List<IObserverCenterPlugin> plugins;
	
	/** Viewport refresh timer. */
	private Timer vptimer;
	
	/** Plugin refresh timer. */
	private Timer plugintimer;
	
	/** The class loader */
	private ClassLoader classloader;
	
	/** Additional IDataView objects */
	private Map externaldataviews;
	
	/** Selected dataview name */
	private String selecteddataviewname;
	
	/** Perspectives */
	private Map<String, IPerspective> perspectives;
	
	/** Selected Perspective */
	private IPerspective selectedperspective;
	
	/** The current space */
//	private Space2D space;
	private AbstractEnvironmentSpace space;
	
	//TODO: move to Perspective!
	/** Area size of the space */
//	private IVector2 areasize;
	
	/** Selected object listeners */
	protected List selectedObjectListeners;
	
	/** The clock listener for sync gui updates. */
	protected IChangeListener	clocklistener;
	
	/** Kill the application on exit. */
	protected boolean	killonexit;
	
	protected IClockService clock;
	
	/** Flag to indicate that observer is disposed. */
	protected boolean	disposed;

	/**
	 * 
	 */
	public ObserverCenter()
	{
	}
	
	/** Starts an observer center.
	 *  
	 *  @param title title of the observer window
	 *  @param space the space being observed
	 *  @param classloader the application class loader for loading resources (images etc.)
	 *  @param plugins custom plugins used in the observer
	 */
	public void startObserver(final String title, final IEnvironmentSpace space, ClassLoader classloader, boolean killonexit)
	{
//		if(space.getExternalAccess().getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//			System.out.println("starting observer: "+this);
		this.plugins = new ArrayList<IObserverCenterPlugin>();
		selectedObjectListeners = Collections.synchronizedList(new ArrayList());
		this.space = (AbstractEnvironmentSpace)space;
		this.killonexit	= killonexit;
		perspectives = Collections.synchronizedMap(new HashMap());
		externaldataviews = Collections.synchronizedMap(new HashMap());
	
		this.classloader = classloader;
		Map spaceviews = space.getDataViews();
		if(!spaceviews.isEmpty())
			selecteddataviewname = (String)spaceviews.keySet().iterator().next();
		activeplugin = null;
		
		space.getExternalAccess().searchService( new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM))
			.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(IClockService result)
			{
				clock = result;
			}
		});
		
		Runnable init = new Runnable()
		{
			public void run()
			{
//				if(space.getExternalAccess().getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//					System.out.println("starting observer2: "+this);
				
				if(disposed)
					return;
				mainwindow = new ObserverCenterWindow(title);
//				loadPlugins(cplugins);
				
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
				
				vptimer = new Timer(33, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
//						System.out.println("update viewport by timer");
						updateDisplay();
					}
				});
				clocklistener	= new IChangeListener()
				{
					boolean repainting	= false;
					public void changeOccurred(final jadex.commons.ChangeEvent event)
					{
						if(!repainting && IClock.EVENT_TYPE_NEXT_TIMEPOINT.equals(event.getType()))
						{
							repainting	= true;
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									repainting	= false;
//									System.out.println("update viewport by clock: "+event);
									updateDisplay();
								}
							});
						}
					}
				};

				JMenu viewportMenu = new JMenu("Viewport Refresh");
				group = new ButtonGroup();
				
				for(int i = 0; i < VIEWPORT_RATES.length; ++i)
				{
					JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ViewportRefreshAction(VIEWPORT_RATES[i]));
					if (VIEWPORT_RATES[i] == DEFAULT_VIEWPORT_RATE)
					{
						item.setSelected(true);
						item.getAction().actionPerformed(null);
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
//						System.out.println("activeplugin: "+activeplugin);
						IObserverCenterPlugin apl = activeplugin;
						if(apl != null)
							apl.refresh();
					}
				});
				plugintimer.start();
								
//				//TODO: find a better solution for closing apps in the Background
//				mainwindow.addWindowListener(new WindowAdapter() {
//		            
//		            public void windowClosing(WindowEvent e) {
////						System.out.println("window closing!");
//						IPerspective p = getSelectedPerspective();
//						if(p instanceof Perspective3D)
//						{
//							((Perspective3D) p).getViewport().stopApp();
//							perspectives.remove(p);
//						}
//						
//						// Close all the Apps in the Background
//						Set<String> keys = perspectives.keySet();
//						
//						for(String key : keys)
//						{
//							IPerspective pp = perspectives.get(key);
//							if (pp instanceof Perspective3D)
//							{
//								((Perspective3D) pp).getViewport().stopApp();
//							}
//							
//						}
//		            }
//		        });
				mainwindow.addWindowListener(new ObserverWindowController());
				
				mainwindow.addWindowStateListener(new WindowStateListener()
				{
					public void windowStateChanged(WindowEvent e)
					{
						IPerspective p = getSelectedPerspective();
						if (p instanceof Perspective2D)
						{
							((Perspective2D) p).getViewport().refreshCanvasSize();
						}
					}
				});
			}
		};
		
		if(EventQueue.isDispatchThread())
		{
			init.run();
		}
		else
		{
//			try
//			{
//				EventQueue.invokeAndWait(init);
				EventQueue.invokeLater(init);
//			}
//			catch (InterruptedException e)
//			{
//			}
//			catch (InvocationTargetException e)
//			{
//			}
		}
	}
	
//	//TODO:Move to Perspective!
//	/**
//	 * Returns the area size.
//	 * 
//	 * @return area size
//	 */
//	public IVector3 getAreaSize()
//	{
//		return space.getAreaSize3d().copy();
//	}
	
	/**
	 * Adds an additional dataview.
	 * @param name name of the dataview
	 * @param dataview an additional dataview
	 */
	public void addDataView(String name, IDataView dataview)
	{
		synchronized(externaldataviews)
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
		if(dataview == null)
			dataview = (IDataView)externaldataviews.get(selecteddataviewname);
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
		selectedperspective.setSelectedObject(null);
	}
	
	/**
	 * Adds a perspective.
	 * @param name name of the perspective
	 * @param perspective the perspective
	 */
	public IFuture<Void>	addPerspective(final String name, final IPerspective perspective)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		if(SwingUtilities.isEventDispatchThread())
		{
			Exception e	= null;
			synchronized(perspectives)
			{
				try
				{
					perspective.setObserverCenter(this);
					perspective.setName(name);
					perspectives.put(name, perspective);
					if(perspectives.size() == 1)
					{
						setSelectedPerspective(name);
					}
				}
				catch(Exception ex)
				{
					e	= ex;
				}
			}
			if(e!=null)
			{
				ret.setException(e);
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() 
				{
					Exception e	= null;
					synchronized(perspectives)
					{
						try
						{
							perspective.setObserverCenter(ObserverCenter.this);
							perspective.setName(name);
							perspectives.put(name, perspective);
							if(perspectives.size() == 1)
							{
								setSelectedPerspective(name);
							}
						}
						catch(Exception ex)
						{
							e	= ex;
						}
					}
					if(e!=null)
					{
						ret.setException(e);
					}
					else
					{
						ret.setResult(null);
					}
				}
			});
		}
		
		return ret;
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
	//TODO: Clean up here!
	public void setSelectedPerspective(final String name)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			IPerspective selp = null;
			synchronized(perspectives)
			{
				selp = getSelectedPerspective();
			}
			
			if(selp!=null)
			{
				if(selp instanceof Perspective3D)
				{
					((Perspective3D)selp).getViewport().pauseApp();
					// todo: Hack!!!!
					try {
			            Thread.sleep(1000);
			        } catch (InterruptedException ex) {
			        }
				}
			}
		
			IPerspective perspective = (IPerspective)perspectives.get(name);
			if (perspective instanceof Perspective2D)
			{
				((Perspective2D) perspective).flushRenderInfo();
			}
			perspective.setObserverCenter(this);
			selectedperspective = perspective;
			mainwindow.setPerspectiveView(perspective.getView());
			perspective.setSelectedObject(null);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() 
				{
					synchronized(perspectives)
					{
						System.out.println("------------------------RUNNABLE C_HANGE---------------------");
						IPerspective perspective = (IPerspective)perspectives.get(name);
						perspective.setObserverCenter(ObserverCenter.this);
						selectedperspective = perspective;
						mainwindow.setPerspectiveView(perspective.getView());
						perspective.setSelectedObject(null);
					}
				}
			});
		}
	}
	
	/**
	 * Sets the OpenGL mode for a perspective
	 * @param name name of the perspective
	 * @param opengl true to activate OpenGL mode
	 */
	public void setOpenGLMode(final String name, final boolean opengl)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() 
			{
				synchronized(perspectives)
				{
					IPerspective perspective = (IPerspective)perspectives.get(name);
					double z = 1.0;
					IVector2 ps = null;
					if (perspective instanceof Perspective2D)
					{
						Perspective2D p = (Perspective2D) perspective;
						z = p.getZoom();
						ps = p.getViewport().getPosition();
					}
					
					perspective.setOpenGl(opengl);
					if (name.equals(selectedperspective.getName()))
					{
						mainwindow.setPerspectiveView(selectedperspective.getView());
						selectedperspective.reset();
					}
					
					if (perspective instanceof Perspective2D)
					{
						final Perspective2D p = (Perspective2D) perspective;
						final IVector2 pos = ps;
						final double zoom = z;
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								p.setZoom(zoom);
								p.getViewport().setPosition(pos);
							}
						});
					}
				}
			}
		});
	}
	
//	/**
//	 * Returns the space.
//	 * @return the space
//	 */
//	public Space2D getSpace()
//	{
//		return space;
//	}
	
	/**
	 * Returns the space.
	 * @return the space
	 */
	public AbstractEnvironmentSpace getSpace()
	{
		return space;
	}
	
	/**
	 *  Adds a listener for change of the selected object
	 *  @param object listener
	 */
	public void addSelectedObjectListener(ChangeListener listener)
	{
		selectedObjectListeners.add(listener);
	}
	
	/**
	 *  Removes a listener for change of the selected object
	 *  @param object listener
	 */
	public void removeSelectedObjectListener(ChangeListener listener)
	{
		selectedObjectListeners.remove(listener);
	}
	
	/**
	 * Fires a selected object change event.
	 */
	public void fireSelectedObjectChange()
	{
		synchronized(selectedObjectListeners)
		{
			for (Iterator it = selectedObjectListeners.iterator(); it.hasNext(); )
			{
				ChangeListener l = (ChangeListener) it.next();
				l.stateChanged(new ChangeEvent(this));
			}
		}
	}
	
	/**
	 * Loads all available plugins
	 * @param customplugins custom plugins used in addition to standard plugins
	 */
	public void loadPlugins(final List customplugins)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			internalLoadPlugins(customplugins);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() 
				{
					internalLoadPlugins(customplugins);
				}
			});
		}
	}
	
	protected void internalLoadPlugins(List customplugins)
	{
//		if(space.getExternalAccess().getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//		System.out.println("loading plugins1: "+this);
	
		IObserverCenterPlugin plugin = new IntrospectorPlugin();
		
		// default plugins
		// TODO: remove hard coding
		plugins.add(plugin);
		
	//	if(space.getExternalAccess().getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
	//		System.out.println("loading plugins2: "+this);
		// TODO: port from simsupport
		plugin = new VisualsPlugin();
		plugins.add(plugin);
		
	//	plugin = new EvaluationPlugin();
	//	plugins.add(plugin);
		
		//plugin = new ToolboxPlugin();
		//plugins.add(plugin);
		if(customplugins!=null)
			plugins.addAll(customplugins);
		
		for(int i = 0; i < plugins.size(); ++i)
		{
			addPluginButton(plugins.get(i));
		}
		
		if(plugins.size()>0)
		{
			for(int i=1; i<plugins.size(); i++)
			{
				if(plugins.get(i).isStartOnLoad())
					activatePlugin(this.plugins.get(i));
			}
			// activate at last to set activeplugin to the first
			activatePlugin(plugins.get(0));
		}
	}
	
	/** Adds a plugin to the toolbar.
	 * 
	 *  @param plugin the plugin
	 */
	private void addPluginButton(IObserverCenterPlugin plugin)
	{
		if(!plugin.isVisible())
			return;
			
		String iconPath = plugin.getIconPath();
		if(iconPath == null)
		{
			mainwindow.addToolbarItem(plugin.getName(), new PluginAction(plugin));
		}
		else
		{
			try
			{
//				System.out.println(iconPath);
//				System.out.println(cl.getResource(iconPath));
				BufferedImage image = ImageIO.read(classloader.getResource(iconPath));
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
//		if(space.getExternalAccess().getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//			System.out.println("activating plugin: "+this+", "+plugin);
		IObserverCenterPlugin oldPlugin = activeplugin;
		if(oldPlugin != null)
		{
			if(!oldPlugin.isStartOnLoad())
				oldPlugin.shutdown();
		}

		if(plugin.isVisible())
			mainwindow.setPluginView(plugin.getName(), plugin.getView());
		plugin.start(this);
		activeplugin = plugin;
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
			if(fps==-1)
			{
				delay	= -1;
				putValue(NAME, "Sync on Clock");
			}
			else if(fps==0)
			{
				delay	= 0;
				putValue(NAME, "Off");
			}
			else
			{
				delay = 1000 / fps;
				putValue(NAME, Integer.toString(fps) + " FPS");
			}
		}
		
		public void actionPerformed(final ActionEvent event)
		{
			if(delay==-1)
			{
				space.getExternalAccess().searchService( new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM))
					.addResultListener(new SwingDefaultResultListener(mainwindow)
				{
					public void customResultAvailable(Object result)
					{
						vptimer.stop();
						IClockService	clock	= (IClockService)result;
						clock.addChangeListener(clocklistener);
					}
					public void customExceptionOccurred(Exception exception)
					{
						if(event!=null)	// Called internally with null -> ignore exception
						{
							super.customExceptionOccurred(exception);
						}
					}
				});
			}
			else if(delay==0)
			{
				space.getExternalAccess().searchService( new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM))
					.addResultListener(new SwingDefaultResultListener(mainwindow)
				{
					public void customResultAvailable(Object result)
					{
						vptimer.stop();
						IClockService	clock	= (IClockService)result;
						clock.removeChangeListener(clocklistener);
					}
					public void customExceptionOccurred(Exception exception)
					{
						if(event!=null)	// Called internally with null -> ignore exception
						{
							super.customExceptionOccurred(exception);
						}
					}
				});
			}
			else
			{
				space.getExternalAccess().searchService( new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM))
					.addResultListener(new SwingDefaultResultListener(mainwindow)
				{
					public void customResultAvailable(Object result)
					{
						IClockService	clock	= (IClockService)result;
						clock.removeChangeListener(clocklistener);
						vptimer.setDelay(delay);
						vptimer.start();
					}
					public void customExceptionOccurred(Exception exception)
					{
						if(event!=null)	// Called internally with null -> ignore exception
						{
							super.customExceptionOccurred(exception);
						}
					}
				});
			}
		}
	}
	
	private class ObserverWindowController implements WindowListener
	{
		public void windowActivated(WindowEvent e)
		{
		}
		
		public void windowClosed(WindowEvent e)
		{
//			getSelectedPerspective().shutdown(mainwindow.isFullscreen());
		}
		
		public void windowClosing(WindowEvent e)
		{
			dispose();
			if(killonexit)
			{
				space.getExternalAccess().getExternalAccess(space.getExternalAccess().getId()).killComponent();
			}
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
		assert SwingUtilities.isEventDispatchThread();
		if(disposed)
		{
			// might be called from space.terminate() and from component listener
			return;
		}
		disposed	= true;
		
		if(plugintimer!=null)
			plugintimer.stop();
		
		for (Iterator it = perspectives.values().iterator(); it.hasNext(); )
		{
			IPerspective persp = (IPerspective) it.next();
			if (persp instanceof Perspective2D)
				((Perspective2D) persp).getViewport().dispose();
			else if (persp instanceof Perspective3D)
				((Perspective3D) persp).getViewport().stopApp();
		}
		
		if(clocklistener!=null)
		{
			clock.removeChangeListener(clocklistener);
//			space.getExternalAccess().getServiceProvider().searchService( new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM))
//				.addResultListener(new SwingDefaultResultListener(mainwindow)
//			{
//				public void customResultAvailable(Object result)
//				{
//					IClockService	clock	= (IClockService)result;
//					clock.removeChangeListener(clocklistener);
//				}
//			});
		}
		
		if (vptimer != null)
			vptimer.stop();
		if (plugintimer != null)
			plugintimer.stop();
		if(mainwindow!=null)
			mainwindow.dispose();
	}
	
	/**
	 *  Get the class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
}
