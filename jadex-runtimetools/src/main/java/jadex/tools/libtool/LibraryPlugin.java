package jadex.tools.libtool;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;
import jadex.tools.common.EditableList;
import jadex.tools.common.EditableListEvent;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.common.plugin.IControlCenter;
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
import java.net.URLDecoder;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

/**
 *  The library plugin.
 */
public class LibraryPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"conversation",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/libcenter.png"),
		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/libcenter_sel.png"),
		"help",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/help.gif"),
	});

	//-------- attributes --------
	
	/** The list. */
	protected EditableList classpaths;
	
	/** The library service. */
//	protected ILibraryService libservice;
	
	//-------- methods --------
	
	/** 
	 *  Initialize the plugin.
	 * /
	public void init(IControlCenter jcc)
	{
		super.init(jcc);
		getJCC().getServiceContainer().getService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				libservice = (ILibraryService)result;
			}
		});
	}*/
	
	/**
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy()
	{
		return false;
	}
	
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
		JButton add = new JButton("Add ...");
		add.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
//		JButton fetch = new JButton("Refresh");		
//		fetch.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton remove = new JButton("Remove");
		remove.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		add.setToolTipText("Add a class path entry");
		remove.setToolTipText("Remove one or more selected entries from the classpath");
//		fetch.setToolTipText("Fetch all entries from current class path");
		buts.add(add, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(remove, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
//		buts.add(fetch, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
//			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		final JFileChooser cchooser = new JFileChooser(".");
		cchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		cchooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File name)
			{
				return name.isDirectory() || name.getName().endsWith(".jar");
			}
			public String getDescription()
			{
				return "*.jar";
			}
		});
		cchooser.setMultiSelectionEnabled(true);
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cchooser.showDialog(SGUI.getWindowParent(classview)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File[] files = cchooser.getSelectedFiles();
					ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
					for(int i=0; i<files.length; i++)
					{
						try
						{
							URL url = files[i].toURI().toURL();
							ls.addURL(url);
							classpaths.addEntry(url.toString());
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
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
					ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
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
//		fetch.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				classpaths.removeEntries();
//				java.util.List entries = fetchManagedClasspathEntries();				
//				for(int i=0; i<entries.size(); i++)
//				{
//					classpaths.addEntry((String)entries.get(i));
//				}
//			}
//		});
		classpaths.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				if(e.getType()== TableModelEvent.DELETE && (e instanceof EditableListEvent))
				{
					EditableListEvent ele = (EditableListEvent)e;
					int start = e.getFirstRow();
					int end = e.getLastRow();

					for(int i=0; i<=end-start; i++)
					{
						ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
						if(ele.getData(i)!=null && ((String)ele.getData(i)).length()>0)
						{
							try
							{
								ls.removeURL(new URL(ele.getData(i).toString()));
							}
							catch(MalformedURLException ex)
							{
								System.out.println(ele.getData(i));
								ex.printStackTrace();
							}	
						}
					}
				}
			}
		});
		
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
		lists.add("System Classpath Entries", otherview);

		// Add a library service listener to be informed about library changes.
		getJCC().getServiceContainer().getService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ILibraryService ls = (ILibraryService)result;
				ls.addLibraryServiceListener(new ILibraryServiceListener()
				{
					public void urlAdded(URL url)
					{
						// todo: make synchronized
						if(!classpaths.containsEntry(url.toString()))
							classpaths.addEntry(url.toString());
					}
					public void urlRemoved(URL url)
					{
						// todo: make synchronized
						if(classpaths.containsEntry(url.toString()))
							classpaths.removeEntry(url.toString());
					}
				});
			}
		});
		
		return lists;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
		Property[] ps = props.getProperties("cp");
		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
		for(int i=0; i<ps.length; i++)
		{
			try
			{
				
				File	file = new File(URLDecoder.decode(ps[i].getValue()));
				if(file.exists())
				{
					ls.addURL(file.toURI().toURL());
				}
				else
				{
					ls.addURL(new URL(ps[i].getValue()));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Classpath problem: "+ps[i].getValue());
			}
		}
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
		List urls = ls.getURLs();

		for(int i=0; i<urls.size(); i++)
		{
			URL	url	= (URL) urls.get(i);
			String	urlstring;
			if(url.getProtocol().equals("file"))
			{
				urlstring	= SUtil.convertPathToRelative(url.getPath());
			}
			else
			{
				urlstring	= url.toString();
			}
			
			props.addProperty(new Property("cp", urlstring));
		}
		return props;
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
		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
		URL[]	urls	= (URL[])ls.getURLs().toArray(new URL[ls.getURLs().size()]);

		for(int i=0; i<urls.length; i++)
		{
			ls.removeURL(urls[i]);
		}
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected java.util.List fetchManagedClasspathEntries()
	{
		List ret = new ArrayList();
		// todo: hack!!!
		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class).get(new ThreadSuspendable());
		List urls = ls.getURLs();
		for(Iterator it=urls.iterator(); it.hasNext(); )
		{
			URL	url	= (URL)it.next();
			ret.add(url.toString());
			
//			String file = url.getFile();
//			File f = new File(file);
//			
//			// Hack!!! Above code doesnt handle relative url paths. 
//			if(!f.exists())
//			{
//				File	newfile	= new File(new File("."), file);
//				if(newfile.exists())
//				{
//					f	= newfile;
//				}
//			}
//			ret.add(f.getAbsolutePath());
		}
		return ret;
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected java.util.List fetchOtherClasspathEntries()
	{
		java.util.List	ret	= new ArrayList();

//		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
		// todo: hack
		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class).get(new ThreadSuspendable());
		ClassLoader	cl	= ls.getClassLoader();
		
		List cps = SUtil.getClasspathURLs(cl!=null ? cl.getParent() : null);	// todo: classpath?
		for(int i=0; i<cps.size(); i++)
		{
			URL	url	= (URL)cps.get(i);
			ret.add(url.toString());
			
//			String file = url.getFile();
//			File f = new File(file);
//			
//			// Hack!!! Above code doesnt handle relative url paths. 
//			if(!f.exists())
//			{
//				File	newfile	= new File(new File("."), file);
//				if(newfile.exists())
//				{
//					f	= newfile;
//				}
//			}
//			ret.add(f.getAbsolutePath());
		}
		
		return ret;
	}
}
