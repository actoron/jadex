package jadex.tools.libtool;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.EditableList;
import jadex.commons.gui.EditableListEvent;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

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
		"help",	SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/help.gng")
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
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		this.libservice	= (ILibraryService)service;
		
		// Create class paths view.
		final JPanel classview = new JPanel(new BorderLayout());
		this.classpaths = new EditableList("Class Paths", true);
		libservice.getManagedResourceIdentifiers().addResultListener(new SwingDefaultResultListener<List<IResourceIdentifier>>(LibServiceBrowser.this)
		{
			public void customResultAvailable(List<IResourceIdentifier> result)
			{
//				List entries = (List)result;
				for(int i=0; i<result.size(); i++)
				{
					classpaths.addEntry(result.get(i).getLocalIdentifier().getUrl().toString());
				}
			}
		});			
	
		JScrollPane scroll = new JScrollPane(classpaths);
		classpaths.setPreferredScrollableViewportSize(new Dimension(400, 200));
		JPanel buts = new JPanel(new GridBagLayout());
		JButton add = new JButton("Add ...");
		add.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
//		JButton fetch = new JButton("Refresh");		
//		fetch.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton remove = new JButton("Remove");
		remove.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton ref = new JButton("Refresh");
		ref.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		add.setToolTipText("Add a class path entry");
		remove.setToolTipText("Remove one or more selected entries from the classpath");
//		fetch.setToolTipText("Fetch all entries from current class path");
		buts.add(add, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(remove, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(ref, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
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
					, "Add")==JFileChooser.APPROVE_OPTION)
				{
					final File[] files = cchooser.getSelectedFiles();
					for(int i=0; i<files.length; i++)
					{
						try
						{
							URL url = files[i].toURI().toURL();
							libservice.addToplevelURL(url).addResultListener(new SwingDefaultResultListener<Void>()
							{
								public void customResultAvailable(Void result)
								{
									refresh();
								}
								public void customExceptionOccurred(Exception exception)
								{
									jcc.setStatusText("Adding url failed: "+exception.getMessage());
								}
							});
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
						libservice.removeURLCompletely(new URL(entries[sel[i]]))
							.addResultListener(new SwingDefaultResultListener<Void>()
						{
							public void customResultAvailable(Void result)
							{
								refresh();
							}
						});
					}
					catch(Exception ex)
					{
						jcc.displayError("Library error", "Could not remove url", ex);
//						System.out.println(entries[sel[i]]);
//						ex.printStackTrace();
					}	
				}
			}
		});
		ref.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
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
								libservice.removeURLCompletely(new URL(ele.getData(i).toString()));
							}
							catch(MalformedURLException ex)
							{
								jcc.displayError("Library error", "Could not remove url", ex);
//								System.out.println(ele.getData(i));
//								ex.printStackTrace();
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
		libservice.getNonManagedURLs().addResultListener(new SwingDefaultResultListener(LibServiceBrowser.this)
		{
			public void customResultAvailable(Object result)
			{
				List entries = (List)result;
				for(int i=0; i<entries.size(); i++)
				{
					dlm.addElement(entries.get(i));
				}
			}
		});
		
		final JList otherlist = new JList(dlm);
		JPanel obuts = new JPanel(new BorderLayout());
		JButton refresh = new JButton("Refresh");
		obuts.add("East", refresh);
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				libservice.getNonManagedURLs().addResultListener(new SwingDefaultResultListener(LibServiceBrowser.this)
				{
					public void customResultAvailable(Object result)
					{
						List entries = (List)result;
						DefaultListModel dlm = (DefaultListModel)otherlist.getModel();
						dlm.removeAllElements();
						for(int i=0; i<entries.size(); i++)
						{
							dlm.addElement((String)entries.get(i));
						}
					}
				});
			}
		});
		otherview.add("Center", new JScrollPane(otherlist));
		otherview.add("South", obuts);
		
		this.add("Managed Classpath Entries", classview);
		this.add("System Classpath Entries", otherview);

		// Add a library service listener to be informed about library changes.
		this.listener	= new ILibraryServiceListener()
		{
			public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier rid)
			{
				// todo: make synchronized
				if(!classpaths.containsEntry(rid.getLocalIdentifier().getUrl().toString()))
					classpaths.addEntry(rid.getLocalIdentifier().getUrl().toString());
				return IFuture.DONE;
			}
			public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier rid)
			{
				// todo: make synchronized
				if(classpaths.containsEntry(rid.getLocalIdentifier().getUrl().toString()))
					classpaths.removeEntry(rid.getLocalIdentifier().getUrl().toString());
				return IFuture.DONE;
			}
		};
		libservice.addLibraryServiceListener(listener);
		
		// Todo: remove listener, when tool is closed.
		
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public void refresh()
	{
		libservice.getManagedResourceIdentifiers().addResultListener(new SwingDefaultResultListener<List<IResourceIdentifier>>(LibServiceBrowser.this)
		{
			public void customResultAvailable(List<IResourceIdentifier> result)
			{
				classpaths.removeEntries();
//						List entries = (List)result;
				for(int i=0; i<result.size(); i++)
				{
					classpaths.addEntry(result.get(i).getLocalIdentifier().getUrl().toString());
				}
			}
		});	
	}
	
	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		try
		{
			libservice.removeLibraryServiceListener(listener);
		}
		catch(Exception e)
		{
		}
		
		return IFuture.DONE;
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
	public IFuture<Void> setProperties(Properties props)
	{
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		return Future.getEmptyFuture();
	}
}
