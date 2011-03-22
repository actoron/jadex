package jadex.tools.jcc;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.ChangeEvent;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.SGUI;
import jadex.xml.PropertiesXMLHelper;

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
	protected Map	pccs;

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

	//-------- constructors --------

	/**
	 *  Create a control center.
	 */
	public IFuture	init(IExternalAccess jccaccess, final String[] plugin_classes)
	{
		final Future	ret	= new Future();
		
		this.jccaccess = jccaccess;
		this.plugin_classes	= plugin_classes;
		this.pccs	= new HashMap();
		
		jccaccess.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new IComponentListener()
				{
					public void componentTerminating(ChangeEvent ae)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								if(!killed)
								{
									shutdown();
								}
								window.setVisible(false);
								window.dispose();
							}
						});
					}

					public void componentTerminated(ChangeEvent ae)
					{
					}
				});
				return null;
			}
		});
		
		this.window = new ControlCenterWindow(this);
		
		// Default platform control center for local platform.
		final Future	inited	= new Future();
		this.pcc	= new PlatformControlCenter();
		
		SJCC.getRootAccess(jccaccess).addResultListener(new SwingDelegationResultListener(inited)
		{
			public void customResultAvailable(Object result) throws Exception
			{
				IExternalAccess	platformaccess	= (IExternalAccess)result;
				pccs.put(platformaccess.getComponentIdentifier(), pcc);
				pcc.init(platformaccess, ControlCenter.this, plugin_classes)
					.addResultListener(new SwingDelegationResultListener(inited));
			}
		});
		
		inited.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				// Load settings and open window.
				loadSettings().addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
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
	public IFuture	loadSettings()
	{
		return loadSettings(new File(jccaccess.getComponentIdentifier().getLocalName() + SETTINGS_EXTENSION));
	}
	
	/**
	 *  Load the settings.
	 */
	public IFuture	loadSettings(final File file)
	{
		final Future	ret	= new Future();
		
		// Read project properties
		SServiceProvider.getService(jccaccess.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Properties	props;
				try
				{
					ClassLoader cl = ((ILibraryService)result).getClassLoader();
					FileInputStream fis = new FileInputStream(file);
					props	= (Properties)PropertiesXMLHelper.getPropertyReader().read(fis, cl, null);
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
				
				pcc.setProperties(props).addResultListener(new SwingDelegationResultListener(ret));
			}
		});
		
		return ret;
	}

	/**
	 * Save settings of JCC and all plugins in current project.
	 */
	public IFuture	saveSettings()
	{
		return saveSettings(new File(jccaccess.getComponentIdentifier().getLocalName() + SETTINGS_EXTENSION));
	}
	
	/**
	 * Save settings of JCC and all plugins in current project.
	 */
	public IFuture	saveSettings(final File file)
	{
		final Future	ret	= new Future();
		
		// Get properties of latest platform panel.
		pcc.getProperties().addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
			{
				final Properties	props	= (Properties)result;
				
				// Store window appearance
				Properties windowprops = new Properties();
				windowprops.addProperty(new Property("width", Integer.toString(window.getWidth())));
				windowprops.addProperty(new Property("height", Integer.toString(window.getHeight())));
				windowprops.addProperty(new Property("x", Integer.toString(window.getX())));
				windowprops.addProperty(new Property("y", Integer.toString(window.getY())));
				windowprops.addProperty(new Property("extendedState", Integer.toString(window.getExtendedState())));
				windowprops.addProperty(new Property("jccexit", jccexit != null? jccexit : JCC_EXIT_ASK));
				props.removeSubproperties("window");
				props.addSubproperties("window", windowprops);
				
				// Save properties to file.
				SServiceProvider.getService(jccaccess.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result) throws Exception
					{
						FileOutputStream os = new FileOutputStream(file);
						PropertiesXMLHelper.getPropertyWriter().write(props, os, ((ILibraryService)result).getClassLoader(), null);
						os.close();
						window.getStatusBar().setText("Settings saved successfully: "+ file.getAbsolutePath());
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 * Informs the window if it should dispose its resources.
	 * @return true if the agent has been killed.
	 */
	public boolean	exit()
	{
		assert SwingUtilities.isEventDispatchThread();
		
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
	
				shutdown();
				SJCC.killPlattform(jccaccess, window);
			}
			else if(JOptionPane.NO_OPTION == choice)
			{
				// Save settings if wanted
				if(msg != null && msg.isRemember())
					jccexit = JCC_EXIT_KEEP;
	
				shutdown();
				jccaccess.killComponent();
			}
			// else CANCEL
		}
		
		return killed;
	}
	
	/**
	 *  Do any required cleanup on exit.
	 */
	public void shutdown()
	{
		// todo: make save on exit configurable.
		saveSettings();
		
		for(Iterator it=pccs.keySet().iterator(); it.hasNext(); )
		{
			((PlatformControlCenter)pccs.get(it.next())).dispose();
		}
		killed = true;
		if(cmshandler!=null)
			cmshandler.dispose();
	}

	
	//-------- methods used by platform control centers --------
	
	/**
	 *  Get the cms update handler shared by all tools.
	 */
	public CMSUpdateHandler getCMSHandler()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(cmshandler==null)
		{
			cmshandler	= new CMSUpdateHandler(jccaccess);
		}
		return cmshandler;
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
				public void customResultAvailable(Object result) throws Exception
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

}
