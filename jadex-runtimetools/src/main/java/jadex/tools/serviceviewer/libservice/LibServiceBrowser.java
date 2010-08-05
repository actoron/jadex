package jadex.tools.serviceviewer.libservice;

import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.service.IService;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;
import jadex.tools.common.EditableList;
import jadex.tools.common.EditableListEvent;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.serviceviewer.IServiceViewerPanel;

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
public class LibServiceBrowser	extends	JTabbedPane	implements IServiceViewerPanel
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"help",	SGUI.makeIcon(LibServiceBrowser.class, "/jadex/tools/common/images/help.gif")
	});

	//-------- attributes --------
	
	/** The list. */
	protected EditableList classpaths;
	
	/** The lib service. */
	protected ILibraryService libservice;
	
	/** The lib service. */
	protected ILibraryServiceListener listener;
	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public void	init(IControlCenter jcc, IService service)
	{
		this.libservice	= (ILibraryService)service;
		
		// Create class paths view.
		final JPanel classview = new JPanel(new BorderLayout());
		this.classpaths = new EditableList("Class Paths", true);
		List entries = fetchManagedClasspathEntries();				
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
					final File[] files = cchooser.getSelectedFiles();
					for(int i=0; i<files.length; i++)
					{
						try
						{
							URL url = files[i].toURI().toURL();
							libservice.addURL(url);
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
				final int[] sel = classpaths.getSelectedRows();
				final String[] entries = classpaths.getEntries();				
				for(int i=0; i<sel.length; i++)
				{
					classpaths.removeEntry(entries[sel[i]]);
					try
					{
						libservice.removeURL(new URL("file:///"+entries[sel[i]]));
					}
					catch(MalformedURLException ex)
					{
						System.out.println(entries[sel[i]]);
						ex.printStackTrace();
					}	
				}
			}
		});
		classpaths.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				if(e.getType()== TableModelEvent.DELETE && (e instanceof EditableListEvent))
				{
					final EditableListEvent ele = (EditableListEvent)e;
					final int start = e.getFirstRow();
					final int end = e.getLastRow();

					for(int i=0; i<=end-start; i++)
					{
						if(ele.getData(i)!=null && ((String)ele.getData(i)).length()>0)
						{
							try
							{
								libservice.removeURL(new URL(ele.getData(i).toString()));
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
		final DefaultListModel dlm = new DefaultListModel();
		entries = fetchOtherClasspathEntries(libservice);
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
				List entries = fetchOtherClasspathEntries(libservice);
				DefaultListModel dlm = (DefaultListModel)otherlist.getModel();
				dlm.removeAllElements();
				for(int i=0; i<entries.size(); i++)
				{
					dlm.addElement((String)entries.get(i));
				}
			}
		});
		otherview.add("Center", new JScrollPane(otherlist));
		otherview.add("South", obuts);
		
		this.add("Managed Classpath Entries", classview);
		this.add("System Classpath Entries", otherview);

		// Add a library service listener to be informed about library changes.
		this.listener	= new ILibraryServiceListener()
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
		};
		libservice.addLibraryServiceListener(listener);
		
		// Todo: remove listener, when tool is closed.
	}
	
	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public void shutdown()
	{
		libservice.removeLibraryServiceListener(listener);
	}

	
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return this;
	}
		
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "libservicebrowser";
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected java.util.List fetchManagedClasspathEntries()
	{
		List ret = new ArrayList();
		// todo: hack!!!
		List urls = libservice.getURLs();
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
	protected java.util.List fetchOtherClasspathEntries(ILibraryService ls)
	{
		java.util.List	ret	= new ArrayList();

//		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class);
		// todo: hack
//		ILibraryService ls = (ILibraryService)getJCC().getServiceContainer().getService(ILibraryService.class).get(new ThreadSuspendable());
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
