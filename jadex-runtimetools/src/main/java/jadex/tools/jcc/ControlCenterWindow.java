package jadex.tools.jcc;

import jadex.base.gui.AboutDialog;
import jadex.base.gui.StatusBar;
import jadex.bridge.IVersionInfo;
import jadex.commons.BrowserLauncher;
import jadex.commons.SUtil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

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
	
    /** The status bar. */
	protected StatusBar statusbar;
	
    /** The tabs for the single platform panels. */
	protected JTabbedPane	tabs;
	
	//-------- constructors --------
	
	/**
	 *  Create a new control center window.
	 */
	public ControlCenterWindow(ControlCenter main)
	{
		this.controlcenter = main;
		this.setTitle("Jadex Control Center "
			+ IVersionInfo.RELEASE_NUMBER + " (" + IVersionInfo.RELEASE_DATE_TEXT + "): "
			+ controlcenter.getJCCAccess().getComponentIdentifier().getName());
	
		getContentPane().setLayout(new BorderLayout());
	
		statusbar = new StatusBar();
		getContentPane().add("South", statusbar);

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
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if(controlcenter.exit())
				{
					dispose();
					setVisible(false);
				}
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
				JOptionPane.showMessageDialog(ControlCenterWindow.this, SUtil.wrapText(text), errortitle, JOptionPane.ERROR_MESSAGE);
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
				controlcenter.setSaveOnExit(soe.isSelected());
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
	 *  Save the current project settings.
	 *  Asks the user for a project file name.
	 *  @param usename	Use the last name as default?
	 */
	protected void saveSettingsAs()
	{
		if(filechooser.showDialog(this, "Save Settings As")==JFileChooser.APPROVE_OPTION)
		{
			final File f = filechooser.getSelectedFile();
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
							filechooser.setSelectedFile(file);
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
		if(filechooser.showDialog(this, "Load Settings")==JFileChooser.APPROVE_OPTION)
		{
			File file = filechooser.getSelectedFile();
			controlcenter.loadSettings(file);
		}
	}
	
	/**
	 *  Add a platform panel.
	 */
	public void showPlatformPanel(PlatformControlCenter pcc)
	{
		if(tabs==null)
		{
			Container	con	= getContentPane();
			PlatformControlCenterPanel	old	= null;
			for(int i=0; old==null && i<con.getComponentCount(); i++)
			{
				if(con.getComponent(i) instanceof PlatformControlCenterPanel)
					old	= (PlatformControlCenterPanel)con.getComponent(i);
			}
			// Put first panel directly in window.
			if(old==null)
			{
				getContentPane().add(pcc.getPanel(), BorderLayout.CENTER);
				repaint();
			}
			
			// Use tab when second panel is added. 
			else if(old!=pcc.getPanel())
			{
				con.remove(old);
				tabs	= new JTabbedPane();
				tabs.addTab(old.getName(), old);
				tabs.addTab(pcc.getPanel().getName(), pcc.getPanel());
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
				tabs.setSelectedComponent(pcc.getPanel());
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
				BrowserLauncher.openURL("http://jadex-agents.informatik.uni-hamburg.de/xwiki/bin/view/Resources/Online+Documentation");
			}
			catch(IOException e)
			{
			}
		}
	};
}
