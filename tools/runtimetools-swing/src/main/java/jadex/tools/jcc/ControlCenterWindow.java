package jadex.tools.jcc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import jadex.base.gui.AboutDialog;
import jadex.base.gui.StatusBar;
import jadex.bridge.VersionInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.SUtil;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.BrowserLauncher;
import jadex.commons.gui.SGUI;

/**
 *  The main window of the control center.
 */
public class ControlCenterWindow extends JFrame
{
	//-------- constants --------
	
	/**	The dimension for toolbar buttons. */
	protected static final Dimension BUTTON_DIM = new Dimension(32, 32);

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"close_0", SGUI.makeIcon(ControlCenterWindow.class, "/jadex/tools/common/images/close_0.png"),
		"close_1", SGUI.makeIcon(ControlCenterWindow.class, "/jadex/tools/common/images/close_1.png"),
		"close_2", SGUI.makeIcon(ControlCenterWindow.class, "/jadex/tools/common/images/close_2.png")
	});

	//-------- attributes --------
	
	/** The control center. */
	protected ControlCenter controlcenter;

	/** The file chooser. */
	protected JFileChooser filechooser;
	
    /** The status bar. */
	protected StatusBar statusbar;
	
    /** The tabs for the single platform panels. */
	protected JTabbedPane	tabs;
	
    /** The first platform control center (when there are still no tabs). */
	protected PlatformControlCenter	first;
	
	/** The cached window state. */
	protected int	cachedstate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new control center window.
	 */
	public ControlCenterWindow(ControlCenter main)
	{
		this.controlcenter = main;
		this.setTitle("Jadex Control Center "
			+ VersionInfo.getInstance().getVersion() + " (" + VersionInfo.getInstance().getTextDateString() + "): "
			+ controlcenter.getJCCAccess().getIdentifier().getName());
	
		getContentPane().setLayout(new BorderLayout());
	
		statusbar = new StatusBar();
		getContentPane().add("South", statusbar);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
//				if(!Starter.isShutdown())
					controlcenter.exit();
			}
		});
		
		addWindowStateListener(new WindowStateListener()
		{			
			public void windowStateChanged(WindowEvent e)
			{
				cachedstate	= e.getNewState();
			}
		});
	}
	
	//-------- GUI helper methods for plugins --------
	
	/**
	 *  Display an error dialog.
	 * 
	 *  @param errortitle The title to use for an error dialog (required).
	 *  @param errormessage An optional error message displayed before the exception.
	 *  @param exception The exception (if any).
	 */
	public void displayError(final String errortitle, final String errormessage, final Exception exception)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				SGUI.showError(ControlCenterWindow.this, errortitle, errormessage, exception);
			}
		});
	}
	
	/**
	 *  Get the status bar.
	 */
	public StatusBar getStatusBar()
	{
		return statusbar;
	}

	//-------- GUI helper methods for platform control centers --------

	/**
	 *  Create a menubar containing the given menus (if any).
	 */
	public JMenuBar	createMenuBar(JMenu[] pluginbar)
	{
		// Create the menu-bar if necessary.
		JMenuBar menubar	= new JMenuBar();

		final JCheckBoxMenuItem	soe	= new JCheckBoxMenuItem("Save Settings on Exit", controlcenter.isSaveOnExit());
		soe.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final boolean sel = soe.isSelected();
				controlcenter.setSaveOnExit(sel);
				controlcenter.getPCC().getPlatformAccess().searchService( new ServiceQuery<>( ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new IResultListener<ISettingsService>()
				{
					public void resultAvailable(ISettingsService setser)
					{
						setser.setSaveOnExit(sel);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// no problem if settings service is not available
					}
				});
			}
		});
		
		// File menu.
		JMenu file = new JMenu("File");		
		file.add(soe);
		file.add(new JMenuItem(SAVE_SETTINGS));
        file.addSeparator();
		file.add(new JMenuItem(LOAD_SETTINGS));
        file.add(new JMenuItem(SAVE_SETTINGS_AS));
		file.addSeparator();
		file.add(new JMenuItem(EXIT));
		menubar.add(file, 0);	// prepend "File" menu.
		
		// Help menu.
		JMenu help = new JMenu("Help");
		help.add(new JMenuItem(ONLINE_DOC));
		help.add(new JMenuItem(ABOUT));
		menubar.add(help);
		
		for(int i=0; pluginbar!=null && i<pluginbar.length; i++)
			menubar.add(pluginbar[i], i+1);
		
		return menubar;
	}

	//-------- other methods --------
	
	/**
	 *  Get the cached window state.
	 *  getExtendedState() deadlocks when called during shutdown hook.
	 */
	public int	getCachedState()
	{
		return cachedstate;
	}

	/**
	 *  Save the current project settings.
	 *  Asks the user for a project file name.
	 *  @param usename	Use the last name as default?
	 */
	protected void saveSettingsAs()
	{
		if(getFileChooser().showDialog(this, "Save Settings As")==JFileChooser.APPROVE_OPTION)
		{
			final File f = getFileChooser().getSelectedFile();
			// Todo: why invoke later?
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
						else if(!file.getName().toLowerCase().endsWith(ControlCenter.SETTINGS_EXTENSION))
						{
							file = new File(file.getAbsolutePath()+ControlCenter.SETTINGS_EXTENSION);
							getFileChooser().setSelectedFile(file);
						}
						controlcenter.saveSettings(file);
					}
				}
			});
		}
	}

	/**
	 *  Load settings from file.
	 *  Asks the user for a file name.
	 */
	protected void	loadSettings()
	{
		if(getFileChooser().showDialog(this, "Load Settings")==JFileChooser.APPROVE_OPTION)
		{
			File file = getFileChooser().getSelectedFile();
			controlcenter.loadSettings(file);
		}
	}
	
	/**
	 *  Get the file chooser.
	 */
	protected JFileChooser	getFileChooser()
	{
		// Lazy creation to avoid nullpointer when running jenkins build as windows service.
		this.filechooser = new JFileChooser(".");
		filechooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.isDirectory() || f.getName().toLowerCase().endsWith(ControlCenter.SETTINGS_EXTENSION);
			}

			public String getDescription()
			{
				return "JCC Settings Files";
			}
		});
		
		return filechooser;
	}
	
	/**
	 *  Add a platform panel.
	 */
	public void showPlatformPanel(PlatformControlCenter pcc)
	{
		if(tabs==null)
		{
			// Put first panel directly in window.
			if(first==null)
			{
				first	= pcc;
				getContentPane().add(pcc.getPanel(), BorderLayout.CENTER);
				repaint();
			}
			
			// Use tab when second panel is added. 
			else if(first!=pcc)
			{
				getContentPane().remove(first.getPanel());
				tabs	= new JTabbedPane();
				tabs.addTab(first.getPanel().getName(), first.getPanel());
				tabs.setTabComponentAt(tabs.indexOfComponent(first.getPanel()), new TabHeader(first));
				first	= null;
				tabs.addTab(pcc.getPanel().getName(), pcc.getPanel());
				tabs.setTabComponentAt(tabs.indexOfComponent(pcc.getPanel()), new TabHeader(pcc));
				tabs.setSelectedComponent(pcc.getPanel());
				getContentPane().add(tabs, BorderLayout.CENTER);
				repaint();
			}
		}
		else
		{
			int index	= tabs.indexOfComponent(pcc.getPanel());
			if(index!=-1)
			{
				tabs.setSelectedIndex(index);
			}
			else
			{
				tabs.addTab(pcc.getPanel().getName(), pcc.getPanel());
				tabs.setTabComponentAt(tabs.indexOfComponent(pcc.getPanel()), new TabHeader(pcc));
				tabs.setSelectedComponent(pcc.getPanel());
			}
			repaint();
		}
	}
	
	/**
	 *  Close a platform panel.
	 */
	public void closePlatformPanel(PlatformControlCenter pcc)
	{
		if(tabs.indexOfComponent(pcc.getPanel())!=-1)
		{
			tabs.removeTabAt(tabs.indexOfComponent(pcc.getPanel()));
			if(tabs.getTabCount()==1)
			{
				TabHeader	header	= (TabHeader)tabs.getTabComponentAt(0);
				first	= header.getPlatformControlCenter();
				getContentPane().remove(tabs);
				tabs	= null;
				getContentPane().add(first.getPanel(), BorderLayout.CENTER);
			}
			repaint();
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


	final Action SAVE_SETTINGS = new AbstractAction("Save Settings")
	{
		public void actionPerformed(ActionEvent e)
		{
			controlcenter.saveSettings();
		}
	};

	final Action SAVE_SETTINGS_AS = new AbstractAction("Save Settings As...")
	{
		public void actionPerformed(ActionEvent e)
		{
			saveSettingsAs();
		}
	};

	final Action LOAD_SETTINGS = new AbstractAction("Load Settings from File...")
	{
		public void actionPerformed(ActionEvent e)
		{
			loadSettings();
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
				BrowserLauncher.openURL("http://www.activecomponents.org/bin/view/Documentation/Overview");
			}
			catch(IOException e)
			{
			}
		}
	};
	
	//-------- tab component --------
	
	/**
	 *  A component for the tab headers.
	 */
	public class TabHeader	extends JPanel
	{
		//-------- attributes --------
		
		/** The platform control center . */
		protected PlatformControlCenter	pcc;
		
		//-------- constructors --------
		
		/**
		 *  Create a new tab header component
		 */
		public TabHeader(final PlatformControlCenter pcc)
		{
			this.pcc	= pcc;
			this.setOpaque(false);
			JLabel	label	= new JLabel(pcc.getPanel().getName());
			final JLabel	close	= new JLabel(icons.getIcon("close_0"));
			close.addMouseListener(new MouseAdapter()
			{
				boolean	in;
				boolean	down;
				public void mouseEntered(MouseEvent e)
				{
					in=true;
					updateIcon(e);
				}
				public void mouseExited(MouseEvent e)
				{
					in=false;
					updateIcon(e);
				}
				public void mousePressed(MouseEvent e)
				{
					down=in;
					updateIcon(e);
				}
				public void mouseReleased(MouseEvent e)
				{
					if(down && in)
					{
						controlcenter.closePlatform(pcc);
					}
					
					down=false;
					updateIcon(e);
				}
				protected void	updateIcon(MouseEvent e)
				{
					close.setIcon(down ? in ? icons.getIcon("close_2") : icons.getIcon("close_1")
						: (e.getModifiers()&MouseEvent.BUTTON1_MASK)==0 && in ? icons.getIcon("close_1") : icons.getIcon("close_0"));
					close.repaint();
				}
			});
			this.setLayout(new GridBagLayout());
			GridBagConstraints	gbc	= new GridBagConstraints();
			gbc.fill	= GridBagConstraints.NONE;
			gbc.anchor	= GridBagConstraints.CENTER;
			this.add(label, gbc);
			gbc.insets	= new Insets(2, 2, 0, 0);
			this.add(close, gbc);
		}
		
		//-------- methods --------

		/**
		 *  Get the platform control center.
		 */
		public PlatformControlCenter getPlatformControlCenter()
		{
			return pcc;
		}
	}
}
