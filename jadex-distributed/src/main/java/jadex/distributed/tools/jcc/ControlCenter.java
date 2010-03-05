package jadex.distributed.tools.jcc;

import jadex.bridge.IVersionInfo;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.service.IServiceContainer;
import jadex.service.PropertiesXMLHelper;
import jadex.service.library.ILibraryService;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.RememberOptionMessage;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.common.plugin.IControlCenterPlugin;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * The control center.
 */
public class ControlCenter implements IControlCenter
{
	// -------- constants --------

	/** The filename extension for jcc projects. */
	public static final String		JCCPROJECT_EXTENSION	= ".jccproject.xml";

	/** The file for saving the last used project name. */
	public static final String		JCC_PROJECT				= "./jcc.project";

	/** Ask for shutdown on exit. */
	public static final String		JCC_EXIT_ASK			= "ask";

	/** Auto-shutdown on exit. */
	public static final String		JCC_EXIT_SHUTDOWN		= "shutdown";

	/** No shutdown on exit. */
	public static final String		JCC_EXIT_KEEP			= "keep";

	// -------- attributes --------

	/** The service container. */
	protected IServiceContainer container;
	
	/** The plugins (plugin->panel). */
	protected Map					plugins;

	/** The control center window. */
	protected ControlCenterWindow	window;

	/** The current project. */
	protected File					project;

	/** The current project properties. */
	protected Properties			props;

	/** Flag indicating if exit was initiated. */
	protected boolean				killed;

	/** Shutdown action on exit (ask (default), keep, shutdown). */
	protected String				jccexit;

	// -------- constructors --------

	/**
	 * Create a control center.
	 */
	public ControlCenter(IServiceContainer container, final String plugins_prop)
	{
		this.container = container;
		this.plugins = new LinkedHashMap();

		assert Thread.currentThread().getContextClassLoader() != null;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				window = new ControlCenterWindow(ControlCenter.this);

				// Load plugins.
				if(plugins_prop != null)
				{
					Set plugin_set = new HashSet();
					StringTokenizer tokenizer = new StringTokenizer(plugins_prop, ", ");
					while(tokenizer.hasMoreTokens())
					{
						Class plugin_class = null;
						try
						{
							plugin_class = SUtil.class.getClassLoader().loadClass(tokenizer.nextToken().trim());
							if(!plugin_set.contains(plugin_class))
							{
								IControlCenterPlugin p = (IControlCenterPlugin)plugin_class.newInstance();
								plugins.put(p, null);
								plugin_set.add(plugin_class);
								setStatusText("Plugin loaded successfully: "+ p.getName());
								
								// Init non lazy plugin
								if(!p.isLazy())
								{
									initPlugin(p);
								}
							}
						}
						catch(Throwable e)
						{
							// e.printStackTrace();
							String text = SUtil.wrapText("Plugin("+ plugin_class + ") could not be loaded: "+ e.getMessage());
							// JOptionPane.showMessageDialog(window, text,
							// "Plugin Error", JOptionPane.INFORMATION_MESSAGE);
							System.out.println(text);
						}
					}
				}

				if(!plugins.isEmpty())
				{
					// load project
					String proj = null;
					try
					{
						StringBuffer sbuf = new StringBuffer();
						Reader r = new FileReader(JCC_PROJECT);
						char[] cbuf = new char[256];
						int len;
						while((len = r.read(cbuf)) != -1)
							sbuf.append(cbuf, 0, len);
						proj = sbuf.toString();
						r.close();
					}
					catch(IOException e)
					{
					}

					if(proj != null && proj.length()>0)
					{
						try
						{
							File project = new File(proj);
							openProject(project);// , false);
							window.filechooser.setCurrentDirectory(project.getParentFile());
							window.filechooser.setSelectedFile(project);
							window.setVisible(true);
						}
						catch(Exception e)
						{
							proj = null;
						}
					}

					else
					{
						// Use default title, location and plugin
						setCurrentProject(null);
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
						window.setSize(new Dimension((int)(dim.width * 0.6),((int)(dim.height * 0.6))));
						window.setLocation(SGUI.calculateMiddlePosition(window));
						activatePlugin((IControlCenterPlugin)plugins.keySet().iterator().next());
						window.setVisible(true);
						window.setCenterSplit(-1);
					}

					// // Print out startup time (for testing purposes).
					// if(Configuration.getConfiguration().getProperty(Configuration.STARTTIME)!=null)
					// {
					// // Use invokeLater to make sure time is calculated after
					// window is initialized.
					// SwingUtilities.invokeLater(new Runnable()
					// {
					// public void run()
					// {
					// agent.getLogger().info("Platform + JCC total start time: "+(System.currentTimeMillis()
					// -
					// Long.parseLong(Configuration.getConfiguration().getProperty(Configuration.STARTTIME)))+"ms.");
					// }
					// });
					// }
				}
				else
				{
					JOptionPane.showMessageDialog(null, "No plugins found.",
						"No plugins found.", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	//-------- project management --------

	/**
	 * Close the current project.
	 */
	public void closeProject()
	{
		resetPlugins();

		setCurrentProject(null);
	}

	/**
	 * Set the title with respect to the actual project.
	 * 
	 * @param file The project file or null for no project.
	 */
	public void setCurrentProject(File file)
	{
		this.project = file;
		saveLastProject(project);
		if(file != null)
		{
			String fname = file.getName();
			int i = fname.lastIndexOf(JCCPROJECT_EXTENSION);
			if(i > 0)
				fname = fname.substring(0, i);
			setTitle("Project " + fname);
		}
		else
		{
			setTitle("Unnamed project");
		}
	}

	/**
	 * Open a given project.
	 */
	public void openProject(File pd) throws Exception
	{
		// Read project properties
		try
		{
			FileInputStream fis = new FileInputStream(pd);
			props = (Properties)PropertiesXMLHelper.getPropertyReader().read(fis, ((ILibraryService)container
				.getService(ILibraryService.class)).getClassLoader(), null);
//			props = XMLPropertiesReader.readProperties(fis,
//					((ILibraryService)agent.getPlatform().getService(
//							ILibraryService.class)).getClassLoader());
			fis.close();
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			
			final String failed = SUtil.wrapText("Could not open project\n\n"+ e.getMessage());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(window, failed, "Project Error",
						JOptionPane.ERROR_MESSAGE);
				}
			});
			
			throw e;
			// e.printStackTrace();
			// return;
		}

		setCurrentProject(pd);

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

			// Load the console heights.
			Properties consoleheights = windowprops
					.getSubproperty("consoleheights");
			if(consoleheights != null)
			{
				Property[] chps = consoleheights.getProperties("consoleheight");
				Map chs = window.getConsoleHeights();
				for(int i = 0; i < chps.length; i++)
				{
					chs.put(chps[i].getName(), new Integer(chps[i].getValue()));
				}
			}
			boolean conon = windowprops.getBooleanProperty("console_on");
			window.setConsoleEnabled(conon);

			jccexit = windowprops.getStringProperty("jccexit");
		}

		// Configure all active plugins.
		// Todo: support deactivation of plugins to speedup project switching?
		for(Iterator it = plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin p = (IControlCenterPlugin)it.next();
			if(plugins.get(p) != null)
			{
				setPluginProperties(p);
			}
		}

		// Use perspective as set in project...
		IControlCenterPlugin plugin = null;
		String pwnd = props.getStringProperty("perspective");
		if(pwnd != null)
		{
			plugin = getPluginForName(pwnd);
		}
		// ...or use first available plugin.
		if(plugin == null)
		{
			plugin = (IControlCenterPlugin)plugins.keySet().iterator().next();
		}
		activatePlugin(plugin);

		setStatusText("Project opened successfully: " + pd.getName());
	}

	/**
	 * Save settings of JCC and all plugins in current project.
	 */
	public void saveProject()
	{
		if(project != null)
		{
			// Write project properties
			Properties oldprops = props;
			props = new Properties();
			props.addProperty(new Property("perspective", window.getPerspective().getName()));

			// Save window appearance
			Properties windowprops = new Properties();
			windowprops.addProperty(new Property("width", Integer.toString(window.getWidth())));
			windowprops.addProperty(new Property("height", Integer.toString(window.getHeight())));
			windowprops.addProperty(new Property("x", Integer.toString(window.getX())));
			windowprops.addProperty(new Property("y", Integer.toString(window.getY())));
			windowprops.addProperty(new Property("extendedState", Integer.toString(window.getExtendedState())));
			windowprops.addProperty(new Property("console_on", Boolean.toString(window.isConsoleEnabled())));
			windowprops.addProperty(new Property("jccexit", jccexit != null? jccexit : JCC_EXIT_ASK));
			AbstractJCCPlugin.addSubproperties(props, "window", windowprops);

			// Save the console heights.
			Properties consoleheights = new Properties();
			Map chs = window.getConsoleHeights();
			for(Iterator it = chs.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				consoleheights.addProperty(new Property(name, "consoleheight",
						"" + chs.get(name)));
			}

			// Save properties of all plugins.
			for(Iterator it = plugins.keySet().iterator(); it.hasNext();)
			{
				IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
				Properties plugprops = null;
				// Only overwrite active plugin settings.
				if(plugins.get(plugin) != null)
					plugprops = plugin.getProperties();
				// Otherwise keep old settings.
				else if(oldprops != null)
					plugprops = oldprops.getSubproperty(plugin.getName());

				if(plugprops != null)
					AbstractJCCPlugin.addSubproperties(props, plugin.getName(), plugprops);
			}


			try
			{
				// Todo: save project
//				FileOutputStream os = new FileOutputStream(project);
//				XMLPropertiesReader.writeProperties(props, os);
//				os.close();
//				setStatusText("Project saved successfully: "+ project.getAbsolutePath());
				
				// for testing the writer
				FileOutputStream os = new FileOutputStream(project);
				PropertiesXMLHelper.getPropertyWriter().write(props, os, ((ILibraryService)container
					.getService(ILibraryService.class)).getClassLoader(), null);
				os.close();
				setStatusText("Project saved successfully: "+ project.getAbsolutePath());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				final String failed = SUtil
					.wrapText("Could not save data in properties file\n\n"+ e.getMessage());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(window, failed,
							"Properties Error", JOptionPane.ERROR_MESSAGE);
					}
				});
				// e2.printStackTrace();
			}
		}
	}

	/**
	 *  Write the name of the last opened project to './jcc.project' file.
	 */
	protected void saveLastProject(File project)
	{
		try
		{
			Writer w = new FileWriter(JCC_PROJECT);
			w.write(project!=null ? project.getAbsolutePath() : "");
			w.close();
		}
		catch(IOException e)
		{
			final String failed = SUtil.wrapText("Could not save last project\n\n" + e);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(window, failed, "Settings Error",
						JOptionPane.ERROR_MESSAGE);
				}
			});
			
			// e1.printStackTrace();
		}
	}

	// -------- plugin handling --------

	/**
	 * Reset all active plugins. Called when the project is closed.
	 */
	protected void resetPlugins()
	{
		// Reset all plugins, which have a panel associated.
		for(Iterator it = plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
			if(plugins.get(plugin) != null)
			{
				try
				{
					plugin.reset();
				}
				catch(Exception e)
				{
					System.err.println("Exception during reset of JCC-Plug-In "
							+ plugin.getName());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Close all active plugins. Called when the JCC exits.
	 */
	protected void closePlugins()
	{
		// Close all plugins, which have a panel associated.
		for(Iterator it = plugins.keySet().iterator(); it.hasNext();)
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
					System.err.println("Exception while closing JCC-Plug-In "
							+ plugin.getName());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Load properties for a given plugin.
	 */
	protected void setPluginProperties(IControlCenterPlugin plugin)
	{
		Properties pluginprops = props.getSubproperty(plugin.getName());
		if(pluginprops != null)
		{
			try
			{
				plugin.setProperties(pluginprops);
			}
			catch(Exception e)
			{
				// e.printStackTrace();
				plugin.reset();
				final String failed = SUtil.wrapText("Error applying settings to plugin "
					+ plugin.getName() + "\n\n" + e.getMessage());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(window, failed,
							"Plugin Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
		else
		{
//			System.out.println("Plugin has no saved properties: "
//					+ plugin.getName());
			plugin.reset();
		}
	}

	/**
	 * Find a plugin by name.
	 * 
	 * @return null, when plugin is not found.
	 */
	protected IControlCenterPlugin getPluginForName(String name)
	{
		for(Iterator it = plugins.keySet().iterator(); it.hasNext();)
		{
			IControlCenterPlugin plugin = (IControlCenterPlugin)it.next();
			if(name.equals(plugin.getName()))
				return plugin;
		}
		return null;
	}

	/**
	 * Activate a plugin.
	 */
	public void activatePlugin(IControlCenterPlugin plugin)
	{
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// Init the plugin, when not yet inited.
		initPlugin(plugin);
		
		window.setPerspective(plugin);

		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * 
	 */
	protected void initPlugin(IControlCenterPlugin plugin)
	{
		if(plugins.get(plugin) == null)
		{
			try
			{
				plugin.init(this);
				JComponent comp = plugin.getView();
				plugins.put(plugin, comp);

				// Project may be null, when activating default project
				if(project != null)
					setPluginProperties(plugin);

				// Todo: move this code to controlcenterwindow!?
				if(plugin.getHelpID() != null)
					GuiProperties.setupHelp(comp, plugin.getHelpID());

				window.content.add(comp, plugin.getName());
//				window.setPerspective(plugin);

				setStatusText("Plugin activated successfully: "+ plugin.getName());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				final String failed = SUtil.wrapText("Error during init of plugin "+ plugin.getName() + "\n\n" + e);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(window, failed,
							"Plugin Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
	}

	// -------- IControlCenter interface --------

	/**
	 * Set a text to be displayed in the status bar. The text will be removed
	 * automatically after some delay (or replaced by some other text).
	 */
	public void setStatusText(String text)
	{
		window.getStatusBar().setText(text);
	}

	/**
	 * Add a component to the status bar.
	 * 
	 * @param id An id for later reference.
	 * @param comp An id for later reference.
	 */
	public void addStatusComponent(Object id, JComponent comp)
	{
		window.getStatusBar().addStatusComponent(id, comp);
	}

	/**
	 * Remove a previously added component from the status bar.
	 * 
	 * @param id The id used for adding the component.
	 */
	public void removeStatusComponent(Object id)
	{
		window.getStatusBar().removeStatusComponent(id);
	}

	/**
	 * Show the console.
	 * 
	 * @param show True, if console should be shown.
	 */
	public void showConsole(boolean show)
	{
		window.showConsole(show);
	}

	/**
	 * Test if console is shown.
	 * 
	 * @return True, if shown.
	 */
	public boolean isConsoleShown()
	{
		return window.isConsoleShown();
	}

	/**
	 * Set the console height.
	 * 
	 * @param height The console height.
	 */
	public void setConsoleHeight(int height)
	{
		window.setConsoleHeight(height);
	}

	/**
	 * Get the console height.
	 * 
	 * @return The console height.
	 */
	public int getConsoleHeight()
	{
		return window.getConsoleHeight();
	}

	/**
	 * Set the console enable state.
	 * 
	 * @param enabled The enabled state.
	 */
	public void setConsoleEnabled(boolean enabled)
	{
		window.setConsoleEnabled(enabled);
	}

	/**
	 * Test if the console is enabled.
	 * 
	 * @return True, if enabled.
	 */
	public boolean isConsoleEnabled()
	{
		return window.isConsoleEnabled();
	}


	/**
	 * Set the title of the window.
	 * 
	 * @param t The title.
	 */
	protected void setTitle(String t)
	{
		window.setTitle("Jadex Control Center " + IVersionInfo.RELEASE_NUMBER
				+ " (" + IVersionInfo.RELEASE_DATE_TEXT + "): " + t);
	}

	/**
	 * Informs the window if it should dispose its resources.
	 * @return true if the agent has been killed.
	 */
	public boolean exit()
	{
		// Called on swing thread, hence invoke synchronized for JOptionPanel not necessary.
		if(killed)
			return true;

		// When no project is open, ask to save project settings.
		if(project == null)
		{
			String msg = SUtil.wrapText("Do you want to save the current settings as a new project?");
			
			int o = JOptionPane.showConfirmDialog(window, msg, "Save Settings",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(JOptionPane.YES_OPTION == o)
			{
				window.saveProjectAs();
			}
		}

		int choice;
		RememberOptionMessage msg = null;
		if(jccexit == null || jccexit.equals(JCC_EXIT_ASK))
		{
			msg = new RememberOptionMessage(
					"You requested to close the Jadex GUI.\n "
							+ "Do you also want to shutdown the local platform?\n");
			choice = JOptionPane.showConfirmDialog(window, msg,
					"Exit Question", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}
		else if(jccexit.equals(JCC_EXIT_KEEP))
		{
			choice = JOptionPane.NO_OPTION;
		}
		else
		// if(jccexit.equals(JCC_EXIT_SHUTDOWN))
		{
			choice = JOptionPane.YES_OPTION;
		}

		if(JOptionPane.YES_OPTION == choice)
		{
			// Save settings if wanted
			if(msg != null && msg.isRemember())
				jccexit = JCC_EXIT_SHUTDOWN;

			// todo: persist needs to much disk space per model?!
			/*
			 * try { SXML.persistModelCache(); } catch(IOException e) { String
			 * text =
			 * SUtil.wrapText("Could not save model cache: "+e.getMessage());
			 * JOptionPane.showMessageDialog(window, text, "Cache problem",
			 * JOptionPane.ERROR_MESSAGE); }
			 */
			saveProject();
//			closeProject();
			closePlugins();
			killed = true;
			
			container.shutdown(null);
			
		}
		else if(JOptionPane.NO_OPTION == choice)
		{
			// Save settings if wanted
			if(msg != null && msg.isRemember())
				jccexit = JCC_EXIT_KEEP;

			saveProject();
//			closeProject();
			closePlugins();
			killed = true;
			
			container.shutdown(null);
		}
		// else CANCEL

		return killed;
	}

	/**
	 * Get the window.
	 */
	public JFrame getWindow()
	{
		return window;
	}

	/**
	 * Check if a project is active.
	 */
	public boolean hasProject()
	{
		return project != null;
	}

	/**
	 * Get all plugins. As the plugins may not be inited only safe methodes such
	 * as getName() and getIcon() may be called.
	 */
	public IControlCenterPlugin[] getPlugins()
	{
		return (IControlCenterPlugin[])plugins.keySet().toArray(
			new IControlCenterPlugin[plugins.size()]);
	}

	/**
	 *  Display an error dialog.
	 * 
	 *  @param errortitle The title to use for an error dialog (required).
	 *  @param errormessage An optional error message displayed before the exception.
	 *  @param exception The exception (if any).
	 */
	public void displayError(final String errortitle, String errormessage, Exception exception)
	{
		final String	text;
		String	exmsg	= exception==null ? null : exception.getMessage();
		if(errormessage==null && exmsg==null)
		{
			text	= errortitle;
		}
		else if(errormessage!=null && exmsg==null)
		{
			text	= errormessage;
		}
		else if(errormessage==null && exmsg!=null)
		{
			text	= exmsg;
		}
		else// if(errormessage!=null && exmsg!=null)
		{
			text = errormessage + "\n" + exmsg;
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JOptionPane.showMessageDialog(window, SUtil.wrapText(text), errortitle, JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		return container;
	}
}
