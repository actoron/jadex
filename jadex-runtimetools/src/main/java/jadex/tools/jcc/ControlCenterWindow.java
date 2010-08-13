package jadex.tools.jcc;

import jadex.commons.BrowserLauncher;
import jadex.commons.SUtil;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;
import jadex.tools.common.AboutDialog;
import jadex.tools.common.ConfigurationDialog;
import jadex.tools.common.ConsolePanel;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.JadexLogoButton;
import jadex.tools.common.StatusBar;
import jadex.tools.common.plugin.IControlCenterPlugin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *  The main window of the control center.
 */
public class ControlCenterWindow extends JFrame
{
	//-------- constants --------
	
	/**	The dimension for toolbar buttons. */
	protected static final Dimension BUTTON_DIM = new Dimension(32, 32);

	//-------- attributes --------
	
	/** The control center. */
	protected ControlCenter controlcenter;

	/** The file chooser. */
	protected final JFileChooser filechooser;
	
	/** The current perspective. */
	protected IControlCenterPlugin currentperspective;

	/** The layout. */
	protected CardLayout clayout;

	/** The content. */
    protected JPanel	content;

    /** The status bar. */
	protected StatusBar statusbar;
	
	/** The tool bar. */
	protected JToolBar toolbar;
	//protected JToolBar lasttoolbar;	// toolbar of last selected plugin (removed buttons will be readded when plugin is switched)
	
	/** The tool count. */
	protected int	toolcnt;
	
	/** A splitpane for?. */
	protected JSplitPane sp;
	
	/** The console. */
	protected ConsolePanel console;
	
	/** Map for console heights (plugin name -> height). */
	protected Map consoleheights; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new control center window.
	 */
	public ControlCenterWindow(ControlCenter main)
	{
		this.controlcenter = main;
		this.consoleheights = new HashMap();
	
		getContentPane().setLayout(new BorderLayout());
		clayout = new CardLayout();
		content = new JPanel(clayout);
	
		this.console = new ConsolePanel();
		console.setConsoleEnabled(false);
		this.sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		content.setMinimumSize(new Dimension(0,0));
		console.setMinimumSize(new Dimension(0,0));
		sp.add(content);
		sp.add(console);
		getContentPane().add("Center", sp);
		sp.setResizeWeight(1.0);
		
		statusbar = new StatusBar();
		getContentPane().add("South", statusbar);

		this.filechooser = new JFileChooser(".");
		filechooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jccproject.xml");
			}

			public String getDescription()
			{
				return "Jadex Project Files";
			}
		});
		
		//BasicSplitPaneDivider.OneTouchActionHandler 

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				console.close();
				if(controlcenter.exit())
				{
					dispose();
					setVisible(false);
				}
			}
		});
	}

	/**
	 *  Create a toolbar containing the given tools (if any).
	 *  @param template Conta
	 */
	protected void changeToolBar(JComponent[] template, IControlCenterPlugin selplugin)
	{
 		// Setup the tool bar.
		if(toolbar==null)
		{
			toolbar	= new JToolBar("Main Toolbar");
			getContentPane().add(BorderLayout.NORTH, toolbar);
	        // Add standard entries (after gap).
	        toolbar.add(Box.createGlue());
	        toolcnt++;
	        toolbar.addSeparator();
	        toolcnt++;
	        
	        //ButtonGroup bg = new ButtonGroup();
	        IControlCenterPlugin[]	plugins	= controlcenter.getPlugins();
	        for(int i=0; i<plugins.length; i++)
	        {
	            final IControlCenterPlugin plugin = plugins[i];
	        	//final JToggleButton button = new JToggleButton(new PluginAction(plugins[i]));
	        	final JButton button = new JButton(new PluginAction(plugins[i]));
	        	Icon ic = plugin.getToolIcon(selplugin.getName().equals(plugins[i].getName()));
	    	    if(ic!=null)
	    	    	button.setIcon(ic);
	    	    else
	    	    	button.setText(plugins[i].getName());
	    	    button.setText("A");
	            button.putClientProperty("plugin", plugins[i]);
	            button.setBorder(null);
	            button.setText(null);
	            button.setMinimumSize(BUTTON_DIM);
	            button.setHorizontalAlignment(SwingConstants.CENTER);
	            button.setVerticalAlignment(SwingConstants.CENTER);
	            button.setToolTipText(plugins[i].getName());
	            button.getModel().addItemListener(new ItemListener()
	            {
	            	public void itemStateChanged(ItemEvent e)
	            	{
	            		//System.out.println(plugin.getName()+" :"+button.isSelected());
	            		button.setIcon(plugin.getToolIcon(button.isSelected()));
	            	}
	            });
	            if(plugins[i].getHelpID()!=null)
	            	GuiProperties.setupHelp(button, plugins[i].getHelpID());
	            
	            //bg.add(button);
	     	    toolbar.add(button);
	            toolcnt++;
	        }
	        toolbar.addSeparator();
	        toolcnt++;
	        toolbar.add(new JadexLogoButton(toolbar));
	        toolcnt++;
		}
		else
		{
			while(toolbar.getComponentCount()>toolcnt)
			{
//				Component	comp	= toolbar.getComponent(0);
				toolbar.remove(0);
				//if(lasttoolbar!=null)
				//	lasttoolbar.add(comp);
			}
		}

        for(int i=0; template!=null && i<template.length; i++)
            toolbar.add(template[i], i);
        //lasttoolbar	= template;
        
        // Select plugins
        for(int i=0; i<toolbar.getComponentCount(); i++)
        {
        	JComponent comp = (JComponent)toolbar.getComponent(i);
        	if(comp.getClientProperty("plugin")!=null)
        	{
        		IControlCenterPlugin pl = (IControlCenterPlugin)comp.getClientProperty("plugin");
        		((JButton)comp).setIcon(pl.getToolIcon(pl.equals(selplugin)));
        		//((JToggleButton)comp).setSelected(pluginname.equals(comp.getClientProperty("pluginname")));	
        	}
        }

        toolbar.validate();
        toolbar.repaint();
        
        // If toolbar has been dropped out -> pack the window (hack???).
        Container	root	= toolbar;
        while(root.getParent()!=null && !(root instanceof Window))
        	root	= root.getParent();
        if(root!=null && !(root instanceof JFrame))
        {
        	((Window)root).pack();
        }
    }
	
	/**
	 *  Create a menubar containing the given menus (if any).
	 */
	protected JMenuBar createMenuBar(JMenu[] pluginbar)
	{
		// Create the menu-bar if nec essary.
		JMenuBar menubar	= new JMenuBar();

		// File menu.
		JMenu file = new JMenu("File");
		file.add(new JMenuItem(NEW_PROJECT));
		file.add(new JMenuItem(OPEN_PROJECT));
        file.addSeparator();
		file.add(new JMenuItem(SAVE_PROJECT));
        file.add(new JMenuItem(SAVE_PROJECT_AS));
		file.addSeparator();
		file.add(new JMenuItem(SETTINGS));
		file.addSeparator();
		file.add(new JMenuItem(EXIT));
		menubar.add(file, 0);	// prepend "File" menu.
		
		// Help menu.
		JMenu help = new JMenu("Help");
		HelpBroker hb = GuiProperties.setupHelp(this.getContentPane(), "tools.controlcenter");
		if(hb!=null)
		{
			JMenuItem helptopics = new JMenuItem("Help Topics");
			helptopics.addActionListener(new CSH.DisplayHelpFromSource(hb));

			JMenuItem helptrack = new JMenuItem(new ImageIcon(ControlCenter.class
				.getResource("/jadex/tools/common/images/help.gif"), "Help cursor"));

			helptrack.addActionListener(new CSH.DisplayHelpAfterTracking(hb));

			help.add(helptopics);
			help.add(helptrack);
		}
		help.add(new JMenuItem(ONLINE_DOC));
		help.add(new JMenuItem(ABOUT));
		menubar.add(help);
		
		for(int i=0; pluginbar!=null && i<pluginbar.length; i++)
			menubar.add(pluginbar[i], i+1);
		
		return menubar;
	}

	/**
	 * This method may only be called from the swing thread
	 */
	protected void setPerspective(IControlCenterPlugin plugin)
	{		
		if(plugin!=null && plugin!=currentperspective)
		{
			IControlCenterPlugin	oldperspective	= currentperspective;
            currentperspective = plugin;

            // Save console height of old perspective
    		if(oldperspective!=null)
    			consoleheights.put(oldperspective.getName()+".console.height", new Integer(getConsoleHeight()));
    		// Set console height of new perspective
    		Integer ch = (Integer)consoleheights.get(currentperspective.getName()+".console.height");
    		//System.out.println("Found: "+ch);
            if(ch!=null)
            	setConsoleHeight(ch.intValue());
            else
            	setConsoleHeight(0);
            //System.out.println(consoleheights+" "+ch);
            
            try
			{
				// Get menu and toolbar before setting to avoid inconsistent state on error in plugin.
				JMenu[] menu = plugin.getMenuBar();
				JComponent[] tool = plugin.getToolBar();
				setJMenuBar(createMenuBar(menu));
				changeToolBar(tool, plugin);
				clayout.show(content, plugin.getName());
				validate();
				repaint();
			}
			catch(RuntimeException e)
			{
				System.err.println("Error in plugin " + plugin.getName());
				e.printStackTrace();

				// Restore perspective.
				if(oldperspective != null)
				{
					setPerspective(oldperspective);
				}
				// When no perspective:
				// Fallback to empty plugin.
				else
				{
					setJMenuBar(createMenuBar(null));
					changeToolBar(null, plugin);
					clayout.show(content, plugin.getName());
					validate();
					repaint();
				}
			}
		}
	}

	/**
	 *  Get the status bar.
	 */
	public StatusBar getStatusBar()
	{
		return statusbar;
	}

	/**
	 * @return current perspective
	 */
	public IControlCenterPlugin getPerspective()
	{
		return currentperspective;
	}
	
	/**
	 *  Get the center split position.
	 */
	public int getCenterSplit()
	{
		return sp.getDividerLocation();
	}
	
	/**
	 *  Set the center split position.
	 *  @param pos The position (-1 for max).
	 */
	public void setCenterSplit(int pos)
	{
		if(pos==-1)
			pos = sp.getMaximumDividerLocation();
		sp.setDividerLocation(pos);
	}
	
	/**
	 *  Set the console enable state.
	 *  @param enabled The enabled state.
	 */
	public void setConsoleEnabled(boolean enabled)
	{
		console.setConsoleEnabled(enabled);
	}
	
	/**
	 *  Test if the console is enabled.
	 *  @return True, if enabled.
	 */
	public boolean isConsoleEnabled()
	{
		return console.isConsoleEnabled();
	}

	/**
	 *  Show the console.
	 *  (Code simultes a one touch expanable click programmatically,
	 *  see BasicSplitPaneDivider.OneTouchActionHandler)
	 */
	public void showConsole(boolean show)
	{
		boolean shown = isConsoleShown();

		//System.out.println(show+" "+shown);
		
		Insets  insets = sp.getInsets();
		int lastloc = sp.getLastDividerLocation();
	    int currentloc = sp.getUI().getDividerLocation(sp);
		int newloc = currentloc;
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI)sp.getUI()).getDivider();

		if(show && !shown) 
		{
			if(currentloc >= (sp.getHeight() - insets.bottom - divider.getHeight())) 
			{
				int maxloc = sp.getMaximumDividerLocation();
				newloc = lastloc<maxloc? lastloc: maxloc*2/3;
            }
		}
		else if(!show && shown)
		{
		    newloc = sp.getMaximumDividerLocation();
		}

		if(currentloc != newloc) 
		{
			sp.setDividerLocation(newloc);
			sp.setLastDividerLocation(currentloc);
		}
		
		/*if(show) 
		{
			if(sp.getOrientation() == JSplitPane.VERTICAL_SPLIT) 
			{
				if(currentLoc >= (sp.getHeight() - insets.bottom - divider.getHeight())) 
				{
					int maxLoc = sp.getMaximumDividerLocation();
					newLoc = Math.min(lastLoc, maxLoc);
					//((BasicSplitPaneUI)sp.getUI()).setKeepHidden(false);
	            }
				/*else 
				{
					newLoc = insets.top;
					//sp.setKeepHidden(true);
				}* /
			}
			else 
			{
			    if(currentLoc >= (sp.getWidth() - insets.right - divider.getWidth())) 
			    {
			    	int maxLoc = sp.getMaximumDividerLocation();
			    	newLoc = Math.min(lastLoc, maxLoc);
			    	//sp.setKeepHidden(false);
			    }
			    /*else 
			    {
			    	newLoc = insets.left;
			    	//splitPaneUI.setKeepHidden(true);
			    }* /
			}
		}
		else 
		{
			if(sp.getOrientation() == JSplitPane.VERTICAL_SPLIT) 
			{
			    if(currentLoc == insets.top) 
			    {
			    	int maxLoc = sp.getMaximumDividerLocation();
			    	newLoc = Math.min(lastLoc, maxLoc);
			    	//sp.getUI().setKeepHidden(false);
	            }
			    /*else 
			    {
			    	newLoc = sp.getHeight() - divider.getHeight() - insets.top;
			    	//splitPaneUI.setKeepHidden(true);
	            }* /
			}
			else 
			{
			    if(currentLoc == insets.left) 
			    {
			    	int maxLoc = sp.getMaximumDividerLocation();
			    	newLoc = Math.min(lastLoc, maxLoc);
			    	//splitPaneUI.setKeepHidden(false);
	            }
			    /*else 
			    {
			    	newLoc = sp.getWidth() - divider.getWidth() - insets.left;
			    	//splitPaneUI.setKeepHidden(true);
			    }* /
			}
		}*/
	}
	
	/**
	 *  Test if console is shown.
	 */
	public boolean isConsoleShown()
	{
		return getConsoleHeight() != 0;
	}
	
	/**
	 *  Set the console height.
	 *  @param height The console height.
	 */
	public void setConsoleHeight(final int height)
	{
		//System.out.println("old: "+sp.getDividerLocation());
		sp.setDividerLocation(sp.getMaximumDividerLocation()- height);
		//System.out.println("new: "+sp.getDividerLocation());
	}
	
	/**
	 *  Get the console height.
	 *  @return The console height.
	 */
	public int getConsoleHeight()
	{
		return sp.getMaximumDividerLocation() - sp.getDividerLocation();
	}
	
	/**
	 *  Get the console heights.
	 */
	public Map getConsoleHeights()
	{
		return consoleheights;
	}
	
	//-------- project management --------

	/**
	 *  Save the current project.
	 *  If no project is selcted, will ask for filename for new poroject.
	 */
	protected void saveProject()
	{
		if(controlcenter.hasProject())
		{
			controlcenter.saveProject();
		}
		else
		{
			saveProjectAs();
		}
	}

	/**
	 *  Save the current project settings.
	 *  Asks the user for a project file name.
	 *  @param usename	Use the last name as default?
	 */
	protected void saveProjectAs()
	{
		if(filechooser.showDialog(this, "Save Project As")==JFileChooser.APPROVE_OPTION)
		{
			final File f = filechooser.getSelectedFile();

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					File file = f;
					if(file!=null)
					{
						if(file.exists())
						{
							String	msg	= SUtil.wrapText("The file: "+file.getAbsolutePath()+" exists.\n"+
								" Do you want to overwrite the file?");
							int o = JOptionPane.showConfirmDialog(ControlCenterWindow.this, msg,
								"Overwrite Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
							if(JOptionPane.NO_OPTION==o)
								return;
						}
						else if(!file.getName().toLowerCase().endsWith(ControlCenter.JCCPROJECT_EXTENSION))
						{
							file = new File(file.getAbsolutePath()+ControlCenter.JCCPROJECT_EXTENSION);
							filechooser.setSelectedFile(file);
						}
						try
						{
							file.createNewFile();
						}
						catch(IOException e)
						{
						}
		
						if(file.canWrite())
						{
							controlcenter.setCurrentProject(file);
							controlcenter.saveProject();
						}
						else
						{
							JOptionPane.showMessageDialog(ControlCenterWindow.this, "Cannot save project here. The project file: \n"
								+file.getAbsolutePath()+"\n cannot be written", "New Project Error",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
	}

	/**
	 *  Open a project.
	 *  Asks the user for a project file name.
	 */
	protected void openProject()
	{
		if(filechooser.showDialog(this, "Open Project")==JFileChooser.APPROVE_OPTION)
		{
			final File file = filechooser.getSelectedFile();

			SServiceProvider.getService(((AgentControlCenter)controlcenter).getAgent().getServiceProvider(), ILibraryService.class)
				.addResultListener(new SwingDefaultResultListener(ControlCenterWindow.this)
			{
				public void customResultAvailable(Object source, Object result)
				{
					ClassLoader cl = ((ILibraryService)result).getClassLoader();
			
					boolean canopen = file!=null && file.canWrite() && file.getName().toLowerCase().endsWith(ControlCenter.JCCPROJECT_EXTENSION);
					if(canopen)
					{
						controlcenter.saveProject();
						controlcenter.closeProject();
						try
						{
							controlcenter.openProject(file, cl);//, true);
						}
						catch(Exception e)
						{
							canopen = false;
						}
					}
					
					if(!canopen)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String	msg	= SUtil.wrapText("Cannot open the project from file:\n"+file);
								JOptionPane.showMessageDialog(ControlCenterWindow.this, msg, "Cannot open the project",
									JOptionPane.ERROR_MESSAGE);
							}
						});
					}
				}
			});
		}
	}
	
	//-------- menu actions --------

	final Action EXIT = new AbstractAction("Exit")
	{
		public void actionPerformed(ActionEvent e)
		{
			controlcenter.exit();
		}
	};


	final Action SAVE_PROJECT = new AbstractAction("Save Project")
	{
		public void actionPerformed(ActionEvent e)
		{
			saveProject();
		}
	};

	final Action SAVE_PROJECT_AS = new AbstractAction("Save Project As...")
	{
		public void actionPerformed(ActionEvent e)
		{
			saveProjectAs();
		}
	};

	final Action OPEN_PROJECT = new AbstractAction("Open Project...")
	{
		public void actionPerformed(ActionEvent e)
		{
			openProject();
		}
	};

	final Action NEW_PROJECT = new AbstractAction("New Project")
	{
		public void actionPerformed(ActionEvent e)
		{
			controlcenter.saveProject();
			controlcenter.closeProject();
		}
	};

	final Action ABOUT = new AbstractAction("About...")
	{
		public void actionPerformed(ActionEvent ae)
		{
			new AboutDialog(ControlCenterWindow.this);
		}
	};

	final Action ONLINE_DOC = new AbstractAction("WWW...")
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				BrowserLauncher.openURL("http://vsis-www.informatik.uni-hamburg.de/projects/jadex/");
			}
			catch(IOException e)
			{
			}
		}
	};

	final AbstractAction SETTINGS = new AbstractAction("Settings...")
	{
		public void actionPerformed(ActionEvent ae)
		{
			JDialog f = new ConfigurationDialog(ControlCenterWindow.this);
			f.pack();
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			int centerx = (int)size.getWidth()/2;
			int centery = (int)size.getHeight()/2;
			f.setLocation(centerx-f.getWidth()/2, centery-f.getHeight()/2);
			f.setVisible(true);
		}
	};

	/**
	 *  Toolbar action for activating a plugin.
	 */
	class PluginAction extends AbstractAction
	{
		final IControlCenterPlugin plugin;

		/**
		 * Constructor for PluginAction.
		 * @param plugin
		 */
		public PluginAction(IControlCenterPlugin plugin)
		{
			super(plugin.getName());
			this.plugin = plugin;
		}

		/**
		 * @param e
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
		{
			controlcenter.activatePlugin(plugin);
		}

	}
}
