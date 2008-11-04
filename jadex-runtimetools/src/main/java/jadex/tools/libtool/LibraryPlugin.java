package jadex.tools.libtool;

import jadex.bridge.ILibraryService;
import jadex.bridge.Properties;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.EditableList;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.starter.StarterPlugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;

/**
 *  The library plugin.
 */
public class LibraryPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"conversation",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/new_conversation.png"),
		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_conversation_sel.png"),
		"help",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/help.gif"),
	});

	//-------- attributes --------
	
	/** The list. */
	protected EditableList classpaths;
	
	/**
	 * @return "Library Tool"
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Library Tool";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("conversation_sel"): icons.getIcon("conversation");
	}

	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		// Create class paths view.
		
		JTabbedPane lists = new JTabbedPane();
		
		final JPanel classview = new JPanel(new BorderLayout());
		this.classpaths = new EditableList("Class Paths", true);
		java.util.List entries = fetchManagedClasspathEntries();				
		for(int i=0; i<entries.size(); i++)
		{
			classpaths.addEntry((String)entries.get(i));
		}
		JScrollPane scroll = new JScrollPane(classpaths);
		classpaths.setPreferredScrollableViewportSize(new Dimension(400, 200));
		JPanel buts = new JPanel(new GridBagLayout());
		JButton add = new JButton("Add");
		add.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton fetch = new JButton("Refresh");		
		fetch.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
//		JButton clear = new JButton("Clear");
//		clear.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton remove = new JButton("Remove");
		remove.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		add.setToolTipText("Add a class path entry");
		remove.setToolTipText("Remove an entry from the classpath");
		fetch.setToolTipText("Fetch all entries from current class path");
		buts.add(add, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(remove, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(fetch, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		final JFileChooser cchooser = new JFileChooser(".");
		cchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cchooser.showDialog(SGUI.getWindowParent(classview)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File file = cchooser.getSelectedFile();
					classpaths.addEntry(""+file);
					ILibraryService ls = (ILibraryService)getJCC().getAgent().getPlatform().getService(ILibraryService.class);
					try
					{
						ls.addURL(file.toURI().toURL());
					}
					catch(MalformedURLException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] sel = classpaths.getSelectedRows();
				String[] entries = classpaths.getEntries();
				for(int i=0; i<sel.length; i++)
				{
					classpaths.removeEntry(entries[sel[i]]);
					ILibraryService ls = (ILibraryService)getJCC().getAgent().getPlatform().getService(ILibraryService.class);
					try
					{
						ls.removeURL(new URL("file:///"+entries[sel[i]]));
					}
					catch(MalformedURLException ex)
					{
						System.out.println(entries[sel[i]]);
						ex.printStackTrace();
					}					
				}
			}
		});
		fetch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				classpaths.removeEntries();
				java.util.List entries = fetchManagedClasspathEntries();				
				for(int i=0; i<entries.size(); i++)
				{
					classpaths.addEntry((String)entries.get(i));
				}
			}
		});
		/**clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				classpaths.removeEntries();
			}
		});*/
		classview.add("Center", scroll);
		classview.add("South", buts);
		
		final JPanel otherview = new JPanel(new BorderLayout());
		DefaultListModel dlm = new DefaultListModel();
		entries = fetchOtherClasspathEntries();
		for(int i=0; i<entries.size(); i++)
		{
			dlm.addElement(entries.get(i));
		}
		final JList otherlist = new JList(dlm);
		JPanel obuts = new JPanel(new BorderLayout());
		JButton refresh = new JButton("Refresh");
		obuts.add("East", refresh);
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DefaultListModel dlm = (DefaultListModel)otherlist.getModel();
				dlm.removeAllElements();
				java.util.List entries = fetchOtherClasspathEntries();				
				for(int i=0; i<entries.size(); i++)
				{
					dlm.addElement((String)entries.get(i));
				}
			}
		});
		otherview.add("Center", new JScrollPane(otherlist));
		otherview.add("South", obuts);
		
		lists.add("Managed Classpath Entries", classview);
		lists.add("Other Classpath Entries", otherview);

		return lists;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
//		librarytool.setProperties(props);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		return new Properties();
//		return librarytool.getProperties();
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.tools.jcc.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.librarytool";
	}
	
	/**
	 *  Reset the conversation center to an initial state
	 */
	public void	reset()
	{
//		librarytool.reset();
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected java.util.List fetchManagedClasspathEntries()
	{
		List ret = new ArrayList();
		ILibraryService ls = (ILibraryService)getJCC().getAgent().getPlatform().getService(ILibraryService.class);
		List urls = ls.getURLs();
		for(Iterator it=urls.iterator(); it.hasNext(); )
		{
			URL	url	= (URL)it.next();
			String file = url.getFile();
			File f = new File(file);
			
			// Hack!!! Above code doesnt handle relative url paths. 
			if(!f.exists())
			{
				File	newfile	= new File(new File("."), file);
				if(newfile.exists())
				{
					f	= newfile;
				}
			}
			ret.add(f.getAbsolutePath());
		}
		return ret;
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected java.util.List fetchOtherClasspathEntries()
	{
		java.util.List	entries	= new ArrayList();

		ILibraryService ls = (ILibraryService)getJCC().getAgent().getPlatform().getService(ILibraryService.class);
		
		List cps = SUtil.getClasspathURLs(ls.getClassLoader().getParent());	// todo: classpath?
		for(int i=0; i<cps.size(); i++)
		{
			URL	url	= (URL)cps.get(i);
			String file = url.getFile();
			File f = new File(file);
			
			// Hack!!! Above code doesnt handle relative url paths. 
			if(!f.exists())
			{
				File	newfile	= new File(new File("."), file);
				if(newfile.exists())
				{
					f	= newfile;
				}
			}
			entries.add(f.getAbsolutePath());
		}
		
		return entries;
	}
}
