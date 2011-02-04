package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.asynctree.TreePopupListener;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IRemoteFilter;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.TreeExpansionHandler;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ModelTreePanel extends JPanel // JSplitPane
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_addfolder.png"),
		"removepath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_removefolder.png"),
//		"checker",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_checker.png"),
		"refresh",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_refresh.png"),
//		"refresh_menu",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_refresh_small.png"),
		"refresh", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/refresh_component.png"),
		"refresh_tree", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/refresh_tree.png"),
		"overlay_refresh", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		"overlay_refreshtree", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		"overlay_showprops", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/overlay_doc.png"),
		"overlay_showobject", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/overlay_bean.png")
	});
	
	//-------- attributes --------
	
	/** The remote flag. */
	protected final boolean remote;
	
	/** The external access. */
	protected final IExternalAccess	exta;
	
	/** The component tree model. */
	protected final AsyncTreeModel	model;
	
	/** The component tree. */
	protected final JTree	tree;
	
	/** The action for refreshing selected components. */
	protected Action refresh;
	
	/** The action for recursively refreshing selected components. */
	protected Action refreshtree;
	
	/** The remove path action. */
	protected Action removepath;
	
	/** The file chooser. */
	protected JFileChooser filechooser;
	
	/** The iconcache. */
	protected ModelIconCache iconcache;
	
	/** Popup rightclick. */
	protected PopupBuilder pubuilder;
	
	/** The filter. */
	protected IRemoteFilter filefilter;
	
	/** Tree expansion handler remembers open tree nodes. */
	protected ExpansionHandler expansionhandler;
	
	/** The filter popup. */
	protected FileFilterMenuItemConstructor filtercon;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ModelTreePanel(IExternalAccess exta)
	{
		this(exta, false);
	}
	
	/**
	 *  Create a new component tree panel.
	 */
	public ModelTreePanel(final IExternalAccess exta, boolean remote)
	{
		this.setLayout(new BorderLayout());
		
		this.exta	= exta;
		this.remote = remote;
		this.model	= new AsyncTreeModel();
		this.tree	= new JTree(model);
		this.iconcache = new ModelIconCache(exta, tree);
		this.expansionhandler = new ExpansionHandler(tree);
		tree.setCellRenderer(new AsyncTreeCellRenderer());
		tree.addMouseListener(new TreePopupListener());
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(0);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		
		this.add(tree, BorderLayout.CENTER);
		
		new TreeExpansionHandler(tree);
		RootNode root = new RootNode(model, tree);
		model.setRoot(root);
		tree.expandPath(new TreePath(root));
		
		filechooser = new JFileChooser(".");
		filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		filechooser.addChoosableFileFilter(new FileFilter()
		{
			public String getDescription()
			{
				return "Paths or .jar files";
			}

			public boolean accept(File f)
			{
				String name = f.getName().toLowerCase();
				return f.isDirectory() || name.endsWith(".jar");
			}
		});
		
		this.filtercon = new FileFilterMenuItemConstructor();
		this.pubuilder = new PopupBuilder(new Object[]{ADD_PATH, ADD_REMOTEPATH, filtercon});

		this.filefilter = new IRemoteFilter()
		{
			public IFuture filter(Object obj)
			{
				Future ret =  new Future();
				
				if(obj instanceof File)
				{
					File file = (File)obj;
					if(filtercon.isAll() || file.isDirectory())
					{
						ret.setResult(Boolean.TRUE);
					}
					else
					{
						SComponentFactory.isModelType(exta, file.getAbsolutePath(), filtercon.getSelectedComponentTypes())
							.addResultListener(new DelegationResultListener(ret));
//						SComponentFactory.isLoadable(exta, file.getAbsolutePath())
//							.addResultListener(new DelegationResultListener(ret));
					}
				}
				else
				{
					ret.setResult(Boolean.FALSE);
				}
				return ret;
			}
		};

		tree.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
					showPopUp(e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
					showPopUp(e.getX(), e.getY());
			}
		});
		
//		RootNode r = (RootNode)getModel().getRoot();
//		((RootNode)getModel().getRoot()).addChild(new DirNode(r, getModel(), tree, new File("c:"), iconcache, null));
		
		refresh	= new AbstractAction("Refresh", icons.getIcon("refresh"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((ITreeNode)paths[i].getLastPathComponent()).refresh(false);
				}
			}
		};

		refreshtree	= new AbstractAction("Refresh subtree", icons.getIcon("refresh_tree"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((ITreeNode)paths[i].getLastPathComponent()).refresh(true);
				}
			}
		};
		
		removepath	= new AbstractAction("Remove Path", icons.getIcon("removepath"))
		{
			public void actionPerformed(ActionEvent e)
			{
				final ITreeNode	node = (ITreeNode)tree.getLastSelectedPathComponent();
				((RootNode)tree.getModel().getRoot()).removeChild(node);
				
				// todo: jars
				if(exta!=null && node instanceof FileNode)
				{
					SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new SwingDefaultResultListener(ModelTreePanel.this)
					{
						public void customResultAvailable(Object result)
						{
							ILibraryService ls = (ILibraryService)result;
							File file = ((FileNode)node).getFile();
							file = new File(file.getParentFile(), file.getName());
							try
							{
								ls.removeURL(file.toURI().toURL());
							}
							catch(MalformedURLException ex)
							{
								ex.printStackTrace();
							}
//							resetCrawler();
//							((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), node, index);
						}
					});
				}
			}
		};
		
		// Default overlays and popups.
		model.addNodeHandler(new INodeHandler()
		{
			public Icon getOverlay(ITreeNode node)
			{
				return null;
			}

			public Action[] getPopupActions(ITreeNode[] nodes)
			{
				List ret = new ArrayList();
				Icon	base	= nodes[0].getIcon();
				
				if(nodes.length==1)
				{
					if(nodes[0].getParent()==tree.getModel().getRoot())
					{
						Action premovepath = new AbstractAction((String)removepath.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_showprops")}) : (Icon)removepath.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								removepath.actionPerformed(e);
							}
						};
						ret.add(premovepath);
					}
				}
				
				Action	prefresh	= new AbstractAction((String)refresh.getValue(Action.NAME),
					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refresh")}) : (Icon)refresh.getValue(Action.SMALL_ICON))
				{
					public void actionPerformed(ActionEvent e)
					{
						refresh.actionPerformed(e);
					}
				};
				ret.add(prefresh);
				Action	prefreshtree	= new AbstractAction((String)refreshtree.getValue(Action.NAME),
					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refreshtree")}) : (Icon)refreshtree.getValue(Action.SMALL_ICON))
				{
					public void actionPerformed(ActionEvent e)
					{
						refreshtree.actionPerformed(e);
					}
				};
				ret.add(prefreshtree);
			
				return (Action[])ret.toArray(new Action[0]);
			}

			public Action getDefaultAction(final ITreeNode node)
			{
				Action	ret	= null;
//				if(node.hasProperties())
//				{
//					ret	= showprops;
//				}
				return ret;
			}
		});
		
		addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyReleased(KeyEvent e)
			{
				if(KeyEvent.VK_F5==e.getKeyCode())
					((ITreeNode)getModel().getRoot()).refresh(true);
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
		});
	}
	
	/**
	 *  Dynamically create a new menu item structure for starting components.
	 */
	class FileFilterMenuItemConstructor implements IMenuItemConstructor
	{
		/** Constant for select all menu item. */
		public static final String SELECT_ALL = "all";
		
		/** The menu. */
		protected JMenu menu;
		
		/** The supported file types to menu items. */
		protected Map filetypes;
		
		/**
		 *  Create a new filter menu item constructor.
		 */
		public FileFilterMenuItemConstructor()
		{
			menu = new JMenu("File Filter");
			filetypes = new HashMap();
			JCheckBoxMenuItem all = new JCheckBoxMenuItem();
			menu.add(all);
			menu.addSeparator();
			filetypes.put(SELECT_ALL, all);
			
			all.setAction(new AbstractAction("All files")
			{
				public void actionPerformed(ActionEvent e)
				{
					for(int i=2; i<menu.getItemCount(); i++)
					{
						JMenuItem item = (JMenuItem)menu.getItem(i);
						if(item!=null)
							item.setEnabled(!isAll());
					}
					((ITreeNode)getModel().getRoot()).refresh(true);
				}
			});
			
			// Init menu
			getMenuItem();
		}
		
		/**
		 * 
		 */
		public boolean isAll()
		{
			return ((JCheckBoxMenuItem)filetypes.get(SELECT_ALL)).isSelected();
		}
		
		/**
		 *  Get all selected 
		 */
		public List getSelectedComponentTypes()
		{
			List ret = new ArrayList();
			
//			if(!isAll())
			{
				for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
				{
					String key = (String)it.next();
//					if(!SELECT_ALL.equals(key))
					{
						Object val = filetypes.get(key);
						if(val instanceof JCheckBoxMenuItem)
						{
							JCheckBoxMenuItem cb = (JCheckBoxMenuItem)val;
							if(cb.isSelected())
							{
								ret.add(key);
							}
						}
					}
				}
			}
			
			return ret;
		}
		
		/**
		 *  Select a set of menu items.
		 */
		public void setSelectedComponentTypes(Set selected)
		{
			for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
				
				Object val = filetypes.get(key);
				if(val instanceof JCheckBoxMenuItem)
				{
					JCheckBoxMenuItem cb = (JCheckBoxMenuItem)val;
					cb.setSelected(selected.contains(key));
				}
			}
		}
		
		/**
		 *  Get or create a new menu item (struture).
		 *  @return The menu item (structure).
		 */
		public JMenuItem getMenuItem()
		{
			if(isEnabled())
			{
				SServiceProvider.getServices(exta.getServiceProvider(), 
					IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						Collection facts = (Collection)result;
						
						Set supported = new HashSet();
						supported.add(SELECT_ALL);
						if(facts!=null)
						{
							for(Iterator it=facts.iterator(); it.hasNext(); )
							{
								IComponentFactory fac = (IComponentFactory)it.next();
								
								String[] fts = fac.getComponentTypes();
								
								// add new file types
								for(int i=0; i<fts.length; i++)
								{
									supported.add(fts[i]);
									if(!filetypes.containsKey(fts[i]))
									{
										final JCheckBoxMenuItem ff = new JCheckBoxMenuItem(fts[i], true);
										fac.getComponentTypeIcon(fts[i]).addResultListener(new DefaultResultListener()
										{
											public void resultAvailable(Object result)
											{
												ff.setIcon((Icon)result);
											}
										});
										
										menu.add(ff);
										ff.addActionListener(new ActionListener()
										{
											public void actionPerformed(ActionEvent e)
											{
												((ITreeNode)getModel().getRoot()).refresh(true);
											}
										});
										filetypes.put(fts[i], ff);
									}
								}
							}
						}
						
						// remove obsolete filetypes
						for(Iterator it=filetypes.keySet().iterator(); it.hasNext(); )
						{
							Object next = it.next();
							if(!supported.contains(next))
							{
								JMenuItem rem = (JMenuItem)filetypes.get(next);
								menu.remove(rem);
								it.remove();
							}
						}
					}
				});
			}
			
			return isEnabled()? menu: null;
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			return true;
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Add a node handler.
	 */
	public void	addNodeHandler(INodeHandler handler)
	{
		model.addNodeHandler(handler);
	}

	/**
	 *  Get the tree model.
	 */
	public AsyncTreeModel	getModel()
	{
		return model;
	}
	
	/**
	 *  Get the tree.
	 */
	public JTree	getTree()
	{
		return tree;
	}
	
	/**
	 *  Dispose the tree.
	 *  Should be called to remove listeners etc.
	 */
	public void	dispose()
	{
//		access.scheduleStep(new IComponentStep()
//		{
//			@XMLClassname("dispose")
//			public Object execute(IInternalAccess ia)
//			{
//				SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////				ia.getRequiredService("cms")
//					.addResultListener(new SwingDefaultResultListener()
//				{
//					public void customResultAvailable(Object result)
//					{
//						cms	= (IComponentManagementService)result;
//						cms.removeComponentListener(null, listener);				
//					}
//					public void customExceptionOccurred(Exception exception)
//					{
//						// ignore
//					}
//				});
//				return null;
//			}
//		});
		
		getModel().dispose();
	}
	
	/**
	 * Show the popup.
	 * @param x The x position.
	 * @param y The y position.
	 */
	protected void showPopUp(int x, int y)
	{
		TreePath sel = tree.getPathForLocation(x, y);
		if(sel==null)
		{
			tree.clearSelection();
//			System.out.println("show");
			JPopupMenu pop = pubuilder.buildPopupMenu();
			pop.show(this, x, y);
		}
	}
		
	/**
	 *  Add a new path to the explorer.
	 */
	public final Action ADD_PATH = new ToolTipAction("Add Path", icons.getIcon("addpath"),
		"Add a new directory path (package root) to the project structure")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(filechooser.showDialog(SGUI.getWindowParent(ModelTreePanel.this)
				, "Add Path")==JFileChooser.APPROVE_OPTION)
			{
				File file = filechooser.getSelectedFile();
				if(file!=null)
				{
					// Handle common user error of double clicking the directory to add.
					if(!file.exists() && file.getParentFile().exists() && file.getParentFile().getName().equals(file.getName()))
						file	= file.getParentFile();
					if(file.exists())
					{
						// Add file/directory to tree.
//						ITreeNode	node	= getModel().getRoot().addPathEntry(file);
						
						// todo: jars
						if(exta!=null)
						{
							final File fcopy = file;
							SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									ILibraryService ls = (ILibraryService)result;
									File f = new File(fcopy.getParentFile(), fcopy.getName());
									try
									{
//										System.out.println("adding:"+f.toURI().toURL());
										ls.addURL(f.toURI().toURL());
									}
									catch(MalformedURLException ex)
									{
										ex.printStackTrace();
									}
								}
							});
						}
						
						final RootNode root = (RootNode)getModel().getRoot();
						ITreeNode node = createNode(root, model, tree, file, iconcache, filefilter, exta);
						root.addChild(node);
					}
					else
					{
						String	msg	= SUtil.wrapText("Cannot find file or directory:\n"+file);
						JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelTreePanel.this),
							msg, "Cannot find file or directory", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			ITreeNode rm = (ITreeNode)tree.getLastSelectedPathComponent();
			return rm==null && !remote;
		}
	};
	
	/**
	 *  Add a new path to the explorer.
	 */
	public final Action ADD_REMOTEPATH = new ToolTipAction("Add Remote Path", icons.getIcon("addpath"),
		"Add a new remote directory path (package root) to the project structure")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			final String filename = JOptionPane.showInputDialog("Enter remote path");
			if(filename!=null && exta!=null)
			{
				final File fcopy = new File(filename);
				SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						ILibraryService ls = (ILibraryService)result;
						File f = new File(fcopy.getParentFile(), fcopy.getName());
						try
						{
							URL url = f.toURI().toURL();
							if((filename.endsWith("\\") || filename.endsWith("/")) && 
								(!url.toString().endsWith("\\") || url.toString().endsWith("/")))
							{
								// Hack! f.toURI().toURL() does not append when file is not local
								// and it cannot be determined if it is a directory
								url = new URL(url.toString()+"/");
							}
							ls.addURL(url);
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
					}
				});
			}
				
			final RootNode root = (RootNode)getModel().getRoot();
			ITreeNode node = ModelTreePanel.createNode(root, model, tree, new RemoteFile(filename, filename, true), iconcache, filefilter, exta);
			root.addChild(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			ITreeNode rm = (ITreeNode)tree.getLastSelectedPathComponent();
			return rm==null && remote;
//			return true;
		}
	};
	
	/**
	 *  Create a new component node.
	 */
	public static ITreeNode createNode(ITreeNode parent, AsyncTreeModel model, 
		JTree tree, Object value, ModelIconCache iconcache, IRemoteFilter filter, IExternalAccess exta)
	{
		ITreeNode ret = null;
		
		if(value instanceof File)
		{
			File file = (File)value;
			if(file.isDirectory())
			{
				ret = new DirNode(parent, model, tree, file, iconcache, filter);
			}
			else if(parent!=model.getRoot())
			{
				ret = new FileNode(parent, model, tree, file, iconcache);
			}
			else
			{
				ret = new JarNode(parent, model, tree, file, iconcache, filter);
			}
		}
		else if(value instanceof RemoteFile)
		{
			RemoteFile file = (RemoteFile)value;
			if(file.isDirectory())
			{
				ret = new RemoteDirNode(parent, model, tree, file, iconcache, filter, exta);
			}
			else if(parent!=model.getRoot())
			{
				ret = new RemoteFileNode(parent, model, tree, file, iconcache, exta);
			}
			else
			{
				ret = new RemoteJarNode(parent, model, tree, file, iconcache, filter, exta);
			}
		}
		
		if(ret==null)
			throw new IllegalArgumentException("Unknown value: "+value);
		
		return ret;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		// Save tree properties.
		
		ModelExplorerProperties	mep	= new ModelExplorerProperties();
		RootNode root = (RootNode)getTree().getModel().getRoot();
		String[] paths	= root.getPathEntries();
		for(int i=0; i<paths.length; i++)
			paths[i]	= SUtil.convertPathToRelative(paths[i]);
		mep.setRootPathEntries(paths);
		mep.setSelectedNode(getTree().getSelectionPath()==null ? null
			: NodePath.createNodePath((FileNode)getTree().getSelectionPath().getLastPathComponent()));
		List	expanded	= new ArrayList();
		Enumeration exp = getTree().getExpandedDescendants(new TreePath(root));
		if(exp!=null)
		{
			while(exp.hasMoreElements())
			{
				TreePath	path	= (TreePath)exp.nextElement();
				if(path.getLastPathComponent() instanceof FileNode)
				{
					expanded.add(NodePath.createNodePath((FileNode)path.getLastPathComponent()));
				}
			}
		}
		mep.setExpandedNodes((NodePath[])expanded.toArray(new NodePath[expanded.size()]));
		// todo: remove ThreadSuspendable()
		ClassLoader cl = ((ILibraryService)SServiceProvider.getService(exta.getServiceProvider(), 
			ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable())).getClassLoader();
		String	treesave	= JavaWriter.objectToXML(mep, cl);	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
		props.addProperty(new Property("tree", treesave));
				
		// Save the last loaded file.
		File sf = filechooser.getSelectedFile();
		if(sf!=null)
		{
			String	lastpath	= SUtil.convertPathToRelative(sf.getAbsolutePath());
			props.addProperty(new Property("lastpath", lastpath));
		}

		// Save refresh/checking flags.
//		props.addProperty(new Property("refresh", Boolean.toString(refresh)));
		
		// Save the state of file filters
		Properties	filterprops	= new Properties(null, "filter", null);
//		filtercon.isAll();
//		filterprops.addProperty(new Property("all", ""+filtercon.isAll()));
		List ctypes = filtercon.getSelectedComponentTypes();
		for(int i=0; i<ctypes.size(); i++)
		{
			String ctype = (String)ctypes.get(i);
			filterprops.addProperty(new Property(ctype, "true"));
		}
		props.addSubproperties(filterprops);
		
		return props;
	}

	/**
	 *  Update tool from given properties.
	 */
	public void setProperties(final Properties props)
	{
//		refresh	= false;	// stops crawler task, if any
		
		// Load root node.
		String	treexml	= props.getStringProperty("tree");
		// todo: hack!
		ILibraryService ls = (ILibraryService)SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable());
		if(treexml!=null)
		{
			try
			{
				// todo: hack!
				ClassLoader cl = ls.getClassLoader();
				ModelExplorerProperties	mep	= (ModelExplorerProperties)JavaReader.objectFromXML(treexml, cl); 	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
//				ModelExplorerProperties	mep	= (ModelExplorerProperties)Nuggets.objectFromXML(treexml, cl);
//				this.root	= new RootNode();
				RootNode root = (RootNode)getTree().getModel().getRoot();
				root.removeAll();
				String[] entries = mep.getRootPathEntries();
				for(int i=0; i<entries.length; i++)
				{
					ITreeNode node = createNode(root, model, tree, new File(entries[i]), iconcache, filefilter, exta);
					root.addChild(node);
//					root.addPathEntry(new File(entries[i]));
				}
//				((ModelExplorerTreeModel)getModel()).setRoot(this.root);

				ITreeNode[] childs = root.getChildren();
				for(int i=0; i<childs.length; i++)
				{
					// Todo: support non-file (e.g. url nodes).
					File file = ((FileNode)childs[i]).getFile();
					
					// Hack!!! Build new file object. This strips trailing "/" from jar file nodes.
					file	= new File(file.getParentFile(), file.getName());
		//			String fname = file.getAbsolutePath();
					// Todo: slash is needed for package determination(?)
					// but breaks for jar files...
		//			if(file.isDirectory() && !fname.endsWith(System.getProperty("file.separator", "/"))
		//				&& !file.getName().endsWith(".jar"))
		//			{
		//				fname += "/";
		//			}
//						try
					{
//							ls.addPath(file.getAbsolutePath());
						try
						{
							ls.addURL(file.toURI().toURL());
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
						//					urls.add(file.toURL());
					}
//						catch(MalformedURLException ex)
//						{
//							String failed = SUtil.wrapText("Could not add path\n\n"+ex.getMessage());
//							JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelExplorer.this), failed, "Path Error", JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
//						}
				}
				
				// Select the last selected model in the tree.
				expansionhandler.setSelectedPath(mep.getSelectedNode());

				// Load the expanded tree nodes.
				expansionhandler.setExpandedPaths(mep.getExpandedNodes());

				((AsyncTreeModel)getModel()).fireTreeChanged(root);
			}
			catch(Exception e)
			{
				System.err.println("Cannot load project tree: "+e.getClass().getName());
//				e.printStackTrace();
			}
		}
				
		// Load last selected model.
		String lastpath = props.getStringProperty("lastpath");
		if(lastpath!=null)
		{
			try
			{
				File mo_file = new File(lastpath);
				filechooser.setCurrentDirectory(mo_file.getParentFile());
				filechooser.setSelectedFile(mo_file);
			}
			catch(Exception e)
			{
			}
		}				
				
		// Load refresh/checking flag (defaults to true).
//		refresh	= !"false".equals(props.getStringProperty("refresh"));
//		if(refreshmenu!=null)
//			refreshmenu.setState(this.refresh);
//		resetCrawler();
		
		// Load the filter settings
		Properties	filterprops	= props.getSubproperty("filter");
		if(filterprops!=null)
		{
			Property[] mps = filterprops.getProperties();
			Set selected = new HashSet();
			for(int i=0; i<mps.length; i++)
			{
				if(Boolean.parseBoolean(mps[i].getValue())) 
					selected.add(mps[i].getType());
			}
			filtercon.setSelectedComponentTypes(selected);
		}
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame f =  new JFrame();
		ModelTreePanel mtp = new ModelTreePanel(null);
		f.add(mtp, BorderLayout.CENTER);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
