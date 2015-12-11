package jadex.tools.jcc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.PropertyUpdateHandler;
import jadex.base.gui.RememberOptionMessage;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.xml.PropertiesXMLHelper;

/**
 *  The global control center.
 */
public class ControlCenter
{
	// -------- constants --------

	/** The filename extension for GUI settings. */
	public static final String	SETTINGS_EXTENSION	= ".settings.xml";

	/** Auto-shutdown on exit. */
	public static final String	JCC_EXIT_SHUTDOWN	= "shutdown";

	/** No shutdown on exit. */
	public static final String	JCC_EXIT_KEEP	= "keep";

	/** Ask for shutdown on exit. */
	public static final String	JCC_EXIT_ASK	= "ask";

	// -------- attributes --------

	/** The jcc component. */
	protected IExternalAccess jccaccess;
	
	/** The plugin classes. */
	// Todo: load classes only once!?
	protected String[]	plugin_classes;
	
	/** The platform control centers (cid -> pcc). */
	protected Map<IComponentIdentifier, PlatformControlCenter>	pccs;

	/** The currently displayed platform control center. */
	protected PlatformControlCenter	pcc;

	/** The control center window. */
	protected ControlCenterWindow	window;
	
	/** Flag indicating if exit was initiated. */
	protected boolean	killed;

	/** Shutdown action on exit (ask (default), keep, shutdown). */
	protected String	jccexit;

	/** The CMS update handler shared by all tools. */
	protected CMSUpdateHandler	cmshandler;

	/** The property handler. */
	protected PropertyUpdateHandler prophandler;
	
	/** The component icon cache shared by all tools. */
	protected ComponentIconCache	iconcache;
	
	/** The save on exit flag. */
	protected boolean	saveonexit;

	//-------- constructors --------

	/**
	 *  Create a control center.
	 */
	public IFuture<Void>	init(IExternalAccess jccaccess, IExternalAccess platformaccess, final String[] plugin_classes, boolean saveonexit)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		this.jccaccess = jccaccess;
		this.plugin_classes	= plugin_classes;
		this.pccs	= new HashMap<IComponentIdentifier, PlatformControlCenter>();
		this.saveonexit	= saveonexit;
		this.window = new ControlCenterWindow(this);
		
		this.pcc	= new PlatformControlCenter();
		pccs.put(platformaccess.getComponentIdentifier(), pcc);
		pcc.init(platformaccess, ControlCenter.this, plugin_classes)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// Load settings and open window.
				loadSettings().addResultListener(new SwingDelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						// Add PCC to window.
						window.showPlatformPanel(pcc);
						window.setVisible(true);
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}

	//-------- methods called by control center window --------

	/**
	 *  Load the settings.
	 */
	public IFuture<Void>	loadSettings()
	{
		// Only load GUI settings as platform settings are loaded on platform startup and every tool init as required.
		return loadSettings(new File(jccaccess.getComponentIdentifier().getLocalName() + SETTINGS_EXTENSION));
	}
	
	/**
	 *  Load the settings.
	 */
	public IFuture<Void>	loadSettings(final File file)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Read project properties
//		SServiceProvider.getService(jccaccess.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new SwingDelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
			{
				getPCC().getClassLoader(null).addResultListener(new SwingExceptionDelegationResultListener<ClassLoader, Void>(ret)
				{
					public void customResultAvailable(ClassLoader cl)
					{
						Properties	props;
						try
						{
//							ClassLoader cl = ((ILibraryService)result).getClassLoader();
//							ClassLoader cl = getPCC().getClassLoader(null);
							FileInputStream fis = new FileInputStream(file);
							props	= (Properties)PropertiesXMLHelper.read(fis, cl);
							fis.close();
							
							Properties windowprops = props.getSubproperty("window");
							if(windowprops != null)
							{
								int w = windowprops.getIntProperty("width");
								int h = windowprops.getIntProperty("height");
								int x = windowprops.getIntProperty("x");
								int y = windowprops.getIntProperty("y");
								window.setBounds(x, y, w, h);
					
								window.setVisible(true); // otherwise it will not be extended (jdk5)
								int es = windowprops.getIntProperty("extendedState");
								window.setExtendedState(es);
					
								jccexit = windowprops.getStringProperty("jccexit");
								
								// Do not override saveonexit agent argument to true by loaded properties.
								saveonexit = saveonexit && windowprops.getBooleanProperty("saveonexit");
							}
						}
						catch(Exception e)
						{
							// Use default values when settings cannot be loaded.
							props	= new Properties();
							
							Dimension	screendim = Toolkit.getDefaultToolkit().getScreenSize();
							// 60% of screen but not smaller than 800x650 but not exceeding screen.
							Dimension	windim	= new Dimension(
								Math.min(Math.max((int)(screendim.width * 0.6), 800), screendim.width),
								Math.min(Math.max((int)(screendim.height * 0.6), 650), screendim.height));
							window.setSize(windim);
							window.setLocation(SGUI.calculateMiddlePosition(window));
						}
						
						pcc.setProperties(props).addResultListener(new SwingDelegationResultListener<Void>(ret));
					}
				});
			}
//		});
		
		return ret;
	}

	/**
	 * Save settings of JCC and all plugins in current project.
	 */
	public IFuture<Void>	saveSettings()
	{
		final Future<Void>	ret	= new Future<Void>();
//		System.out.println("Saving JCC settings");
		// Save settings of GUI and currently selected platform (todo: all platforms?)
		saveSettings(new File(jccaccess.getComponentIdentifier().getLocalName() + SETTINGS_EXTENSION))
			.addResultListener(new SwingDelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("Saving platform settings");
				pcc.savePlatformProperties().addResultListener(new SwingDelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						super.customResultAvailable(result);
					}
					public void customExceptionOccurred(Exception exception)
					{
						super.customExceptionOccurred(exception);
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 * Save settings of JCC and all plugins in current project.
	 */
	public IFuture<Void>	saveSettings(final File file)
	{
		final Future<Void>	ret	= new Future<Void>();
//		System.out.println("Fetching JCC properties.");
		
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Could not save settings: "+exception);
			}
		});
		
		// Get properties of latest platform panel.
//		pcc.getProperties().addResultListener(new TimeoutResultListener<Properties>(5000, jccaccess, new SwingDelegationResultListener(ret)
		pcc.getProperties().addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("Fetched JCC properties.");
				final Properties	props	= (Properties)result;
			
				// Store window appearance
				Properties windowprops = new Properties();
				windowprops.addProperty(new Property("width", Integer.toString(window.getWidth())));
				windowprops.addProperty(new Property("height", Integer.toString(window.getHeight())));
				windowprops.addProperty(new Property("x", Integer.toString(window.getX())));
				windowprops.addProperty(new Property("y", Integer.toString(window.getY())));
				// getExtendedState() deadlocks when shutdown hook is triggered.
				windowprops.addProperty(new Property("extendedState", Integer.toString(window.getCachedState())));
				windowprops.addProperty(new Property("jccexit", jccexit != null? jccexit : JCC_EXIT_ASK));
				windowprops.addProperty(new Property("saveonexit", Boolean.toString(saveonexit)));
				props.removeSubproperties("window");
				props.addSubproperties("window", windowprops);
				
				getPCC().getClassLoader(null).addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ClassLoader cl = (ClassLoader)result;
						try
						{
//							System.out.println("Writing properties.");
							FileOutputStream os = new FileOutputStream(file);
//							PropertiesXMLHelper.getPropertyWriter().write(props, os, ((ILibraryService)result).getClassLoader(), null);
//							PropertiesXMLHelper.getPropertyWriter().write(props, os, getPCC().getClassLoader(null), null);
							PropertiesXMLHelper.write(props, os, cl);
							os.close();
							window.getStatusBar().setText("Settings saved successfully: "+ file.getAbsolutePath());
							ret.setResult(null);
						}
						catch(Exception e)
						{
//							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 * Informs the window if it should dispose its resources.
	 */
	public void	exit()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		if(!killed)
		{
			int choice;
			RememberOptionMessage msg = null;
			if(jccexit==null || jccexit.equals(JCC_EXIT_ASK))
			{
				msg = new RememberOptionMessage("You requested to close the JCC GUI.\n "
					+ "Do you also want to shutdown the local platform?\n");
				choice = JOptionPane.showConfirmDialog(window, msg, "Exit Question",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			}
			else if(jccexit.equals(JCC_EXIT_KEEP))
			{
				choice = JOptionPane.NO_OPTION;
			}
			else // if(jccexit.equals(JCC_EXIT_SHUTDOWN))
			{
				choice = JOptionPane.YES_OPTION;
			}
	
			if(JOptionPane.YES_OPTION == choice)
			{
				// Save settings if wanted
				if(msg!=null && msg.isRemember())
					jccexit = JCC_EXIT_SHUTDOWN;
	
				SJCC.killPlattform(jccaccess, window);
			}
			else if(JOptionPane.NO_OPTION == choice)
			{
				// Save settings if wanted
				if(msg != null && msg.isRemember())
					jccexit = JCC_EXIT_KEEP;
	
				jccaccess.killComponent();
			}
			// else CANCEL
		}
	}
	
	/**
	 *  Do any required cleanup on exit.
	 */
	public IFuture<Void>	shutdown()
	{
//		System.out.println("Control Center shutdown A.");
		final Future<Void>	ret	= new Future<Void>();
		
		Runnable	runnable	= new Runnable()
		{
			public void run()
			{
//				System.out.println("Control Center shutdown B.");
				assert !killed;
				killed = true;
				
				IFuture<Void>	saved;
				if(saveonexit)
					saved	= saveSettings();
				else
					saved	= IFuture.DONE;
				
				saved.addResultListener(new SwingDelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
//						System.out.println("JCC settings saved.");
						
						// Todo: pcc dispose with future?
						CounterResultListener<Void> lis = new CounterResultListener<Void>(pccs.size(), true, 
							new SwingDelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								IFuture<Void>	handlerdisposed;
								if(cmshandler!=null)
									handlerdisposed	= cmshandler.dispose();
								else
									handlerdisposed	= IFuture.DONE;
								
								if(prophandler!=null)
									prophandler.dispose();

								handlerdisposed.addResultListener(new SwingDelegationResultListener<Void>(ret)
								{
									public void customResultAvailable(Void result)
									{
//										System.out.println("CMS handlers disposed.");
//										if(!Starter.isShutdown())
										{
											window.setVisible(false);
											window.dispose();
										}
										ret.setResult(null);
									}
								});
							}
						});
						for(Iterator it=pccs.keySet().iterator(); it.hasNext(); )
						{
							((PlatformControlCenter)pccs.get(it.next())).dispose().addResultListener(lis);
						}
					}
				});
			}
		};
		
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
//		if(Starter.isShutdown())
//		{
//			runnable.run();
//		}
//		else
		{
			SwingUtilities.invokeLater(runnable);
		}
			
		
		return ret;
	}
	
	/**
	 *  Get the save on exit flag.
	 */
	public boolean isSaveOnExit()
	{
		return saveonexit;
	}
	
	/**
	 *  Set the save on exit flag.
	 */
	public void setSaveOnExit(boolean saveonexit)
	{
		this.saveonexit	= saveonexit;
	}
	
	//-------- methods used by platform control centers --------
	
	/**
	 *  Get the cms update handler shared by all tools.
	 */
	public CMSUpdateHandler getCMSHandler()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		if(cmshandler==null)
		{
			cmshandler	= new CMSUpdateHandler(jccaccess);
		}
		return cmshandler;
	}
	
	/**
	 *  Get the property update handler shared by all tools.
	 */
	public PropertyUpdateHandler getPropertyHandler()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		if(prophandler==null)
		{
			prophandler	= new PropertyUpdateHandler(jccaccess);
		}
		return prophandler;
	}
	
	/**
	 *  Get the component icon cache shared by all tools.
	 */
	public ComponentIconCache getIconCache()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		if(iconcache==null)
		{
			iconcache	= new ComponentIconCache(jccaccess);
		}
		return iconcache;
	}

	/**
	 *  Get the control center window.
	 */
	public ControlCenterWindow	getWindow()
	{
		return window;
	}
	
	/**
	 *  Get the JCC access.
	 */
	public IExternalAccess	getJCCAccess()
	{
		return jccaccess;
	}
	
	/**
	 *  Add a new platform control center
	 *  or switch to tab if already exists.
	 */
	public void	showPlatform(IExternalAccess platformaccess)
	{
		final Future	newpcc	= new Future();
		if(pccs.containsKey(platformaccess.getComponentIdentifier()))
		{
			newpcc.setResult(pccs.get(platformaccess.getComponentIdentifier()));
		}
		else
		{
			final PlatformControlCenter	tmp	= new PlatformControlCenter();
			pccs.put(platformaccess.getComponentIdentifier(), tmp);
			tmp.init(platformaccess, this, plugin_classes)
				.addResultListener(new SwingDelegationResultListener(newpcc)
			{
				public void customResultAvailable(Object result)
				{
					newpcc.setResult(tmp);
				}
			});
		}
		
		newpcc.addResultListener(new SwingDefaultResultListener(window)
		{
			public void customResultAvailable(Object result)
			{
				final PlatformControlCenter	pcc2	= (PlatformControlCenter)result;
				
				// Transfer settings from pcc to pcc2
				pcc.getProperties().addResultListener(new SwingDefaultResultListener(window)
				{
					public void customResultAvailable(Object result)
					{
						pcc2.setProperties((Properties)result).addResultListener(new SwingDefaultResultListener(window)
						{
							public void customResultAvailable(Object result)
							{
								pcc	= pcc2;
								window.showPlatformPanel(pcc);
							}
						});
					}
				});
				
			}
		});
	}

	/**
	 *  Close a platform control center.
	 */
	public void closePlatform(final PlatformControlCenter pcc)
	{
		pccs.remove(pcc.getPlatformAccess().getComponentIdentifier());
		window.getStatusBar().setText("Saving platform settings for: "+pcc.getPlatformAccess().getComponentIdentifier().getPlatformName());
		window.closePlatformPanel(pcc);
		
		// Do not save settings when closing remote platform window
		boolean allowed = pcc.getPlatformAccess().getComponentIdentifier().getRoot().equals(jccaccess.getComponentIdentifier().getRoot());
//		System.out.println("allowed: "+allowed);
		
		IFuture	saved	= isSaveOnExit() && allowed? pcc.savePlatformProperties() : IFuture.DONE;
		saved.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				window.getStatusBar().setText("Saved platform settings for: "+pcc.getPlatformAccess().getComponentIdentifier().getPlatformName());
				pcc.dispose();
			}
			public void customExceptionOccurred(Exception exception)
			{
				// Continue anyways.
				window.getStatusBar().setText("Could not save platform settings: "+exception);
				pcc.dispose();
			}
		});
	}
	
	//-------- used for test case --------
	
	/**
	 *  Get the current platform control center.
	 */
	public PlatformControlCenter	getPCC()
	{
		return pcc;
	}
}
