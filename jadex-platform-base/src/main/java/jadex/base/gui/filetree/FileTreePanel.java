package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.asynctree.TreePopupListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.IRemoteFilter;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *  A panel displaying components on the platform as tree.
 */
public class FileTreePanel extends JPanel implements IPropertiesProvider
{
	//-------- attributes --------
	
	/** The remote flag. */
	protected final boolean remote;
	
	/** The external access. */
	protected final IExternalAccess	exta;
	
	/** The component tree model. */
	protected final AsyncTreeModel	model;
	
	/** The component tree. */
	protected final JTree tree;
		
	/** Tree expansion handler remembers open tree nodes. */
	protected ExpansionHandler expansionhandler;
		
	/** The iconcache. */
	protected DelegationIconCache iconcache;
	
	
	/** The node factory. */
	protected INodeFactory factory;
	
	/** The filter. */
	protected DelegationFilter filefilter;

	/** Popup rightclick. */
	protected PopupBuilder pubuilder;
	
	/** The filter popup. */
	protected IMenuItemConstructor mic;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public FileTreePanel(IExternalAccess exta)
	{
		this(exta, false);
	}
	
	/**
	 *  Create a new component tree panel.
	 */
	public FileTreePanel(final IExternalAccess exta, boolean remote)
	{
		this.setLayout(new BorderLayout());
		
		this.exta	= exta;
		this.remote = remote;
		this.model	= new AsyncTreeModel();
		this.tree	= new JTree(model);
		this.expansionhandler = new ExpansionHandler(tree);
		this.filefilter = new DelegationFilter();
		this.iconcache = new DelegationIconCache();
		this.factory = new DefaultNodeFactory();
		
		tree.setCellRenderer(new AsyncTreeCellRenderer());
		tree.addMouseListener(new TreePopupListener());
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(0);
		tree.setRootVisible(false);
		tree.setRowHeight(16);
		
		this.add(tree, BorderLayout.CENTER);
		
		new TreeExpansionHandler(tree);
		RootNode root = new RootNode(model, tree);
		model.setRoot(root);
		tree.expandPath(new TreePath(root));
		
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
	public AsyncTreeModel getModel()
	{
		return model;
	}
	
	/**
	 *  Get the tree.
	 */
	public JTree getTree()
	{
		return tree;
	}
	
	/**
	 *  Get the external access.
	 *  @return the external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return exta;
	}
	
	/**
	 *  Get the remote.
	 *  @return the remote.
	 */
	public boolean isRemote()
	{
		return remote;
	}

	/**
	 *  Set the file filter.
	 *  @param filefilter The file filter.
	 */
	public void setFileFilter(IRemoteFilter filefilter)
	{
		this.filefilter.setFilter(filefilter);
	}
	
	/**
	 *  Set the menu item constructor.
	 *  @param mic The menu item constructor.
	 */
	public void setMenuItemConstructor(IMenuItemConstructor mic)
	{
		this.mic = mic;
	}
	
	/**
	 *  Set the popup builder.
	 *  @param pubuilder The popup builder.
	 */
	public void setPopupBuilder(PopupBuilder pubuilder)
	{
		this.pubuilder = pubuilder;
	}

	/**
	 *  Get the popup builder.
	 *  @return The popup builder.
	 */
	public PopupBuilder getPopupBuilder()
	{
		return pubuilder;
	}

	/**
	 *  Set the iconcache.
	 *  @param iconcache The iconcache to set.
	 */
	public void setIconCache(IIconCache iconcache)
	{
		this.iconcache.setIconCache(iconcache);
	}
	
	/**
	 *  Get the node factory.
	 *  @return The node factory.
	 */
	public INodeFactory getFactory()
	{
		return factory;
	}

	/**
	 *  Set the factory.
	 *  @param factory The factory to set.
	 */
	public void setNodeFactory(INodeFactory factory)
	{
		this.factory = factory;
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
			if(pubuilder!=null)
			{
				JPopupMenu pop = pubuilder.buildPopupMenu();
				pop.show(this, x, y);
			}
		}
	}
	
	/**
	 *  Add a top level node.
	 */
	public void addTopLevelNode(File file)
	{
		assert !remote;
		
		final RootNode root = (RootNode)getModel().getRoot();
		ITreeNode node = factory.createNode(root, model, tree, file, 
			iconcache, filefilter, exta, factory);
		root.addChild(node);
	}
	
	/**
	 *  Add a top level node.
	 */
	public void addTopLevelNode(FileData file)
	{
		assert remote;
		
		final RootNode root = (RootNode)getModel().getRoot();
		ITreeNode node = factory.createNode(root, model, tree, file, 
			iconcache, filefilter, exta, factory);
		root.addChild(node);
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture getProperties()
	{
		final Future ret = new Future();
		final Properties props = new Properties();
		if(remote)
			return new Future(props);
		
		// Save tree properties.
		final TreeProperties	mep	= new TreeProperties();
		RootNode root = (RootNode)getTree().getModel().getRoot();
//		String[] paths	= root.getPathEntries();
//		for(int i=0; i<paths.length; i++)
//			paths[i]	= SUtil.convertPathToRelative(paths[i]);
//		mep.setRootPathEntries(paths);
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
		SServiceProvider.getService(exta.getServiceProvider(), 
			ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ClassLoader cl = ((ILibraryService)result).getClassLoader();
				String	treesave	= JavaWriter.objectToXML(mep, cl);	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
				props.addProperty(new Property("tree", treesave));
						
				// Save the last loaded file.
//				File sf = filechooser.getSelectedFile();
//				if(sf!=null)
//				{
//					String	lastpath	= SUtil.convertPathToRelative(sf.getAbsolutePath());
//					props.addProperty(new Property("lastpath", lastpath));
//				}

				// Save refresh/checking flags.
//				props.addProperty(new Property("refresh", Boolean.toString(refresh)));
				
				// Save the state of file filters
				if(mic instanceof IPropertiesProvider)
				{
					((IPropertiesProvider)mic).getProperties()
						.addResultListener(new SwingDelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							Properties	filterprops	= (Properties)result;
							props.addSubproperties(filterprops);
							ret.setResult(props);
						}
					});
				}
				else
				{
					ret.setResult(props);
				}
//				Properties	filterprops	= new Properties(null, "filter", null);
////				filtercon.isAll();
////				filterprops.addProperty(new Property("all", ""+filtercon.isAll()));
//				List ctypes = filtercon.getSelectedComponentTypes();
//				for(int i=0; i<ctypes.size(); i++)
//				{
//					String ctype = (String)ctypes.get(i);
//					filterprops.addProperty(new Property(ctype, "true"));
//				}
//				props.addSubproperties(filterprops);
//				ret.setResult(props);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Update tool from given properties.
	 */
	public IFuture setProperties(final Properties props)
	{
		final Future ret = new Future();
		
		if(remote)
		{
			ret.setResult(null);
			return ret;
		}
//		refresh	= false;	// stops crawler task, if any
		
		SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService)result;
				
				// Load root node.
				String	treexml	= props.getStringProperty("tree");
				if(treexml==null)
				{
					ret.setResult(null);
				}
				else
				{
					try
					{
						// todo: hack!
						ClassLoader cl = ls.getClassLoader();
						TreeProperties	mep	= (TreeProperties)JavaReader.objectFromXML(treexml, cl); 	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
//						ModelExplorerProperties	mep	= (ModelExplorerProperties)Nuggets.objectFromXML(treexml, cl);
						RootNode root = (RootNode)getTree().getModel().getRoot();
//						root.removeAll();
//						String[] entries = mep.getRootPathEntries();
//						for(int i=0; i<entries.length; i++)
//						{
//							ITreeNode node = factory.createNode(root, model, tree, new File(entries[i]), iconcache, filefilter, exta, factory);
//							root.addChild(node);
//						}

						// Select the last selected model in the tree.
						expansionhandler.setSelectedPath(mep.getSelectedNode());

						// Load the expanded tree nodes.
						expansionhandler.setExpandedPaths(mep.getExpandedNodes());

						root.refresh(true);
						
						// Load last selected model.
//						String lastpath = props.getStringProperty("lastpath");
//						if(lastpath!=null)
//						{
//							try
//							{
//								File mo_file = new File(lastpath);
//								filechooser.setCurrentDirectory(mo_file.getParentFile());
//								filechooser.setSelectedFile(mo_file);
//							}
//							catch(Exception e)
//							{
//							}
//						}				
								
						// Load refresh/checking flag (defaults to true).
//						refresh	= !"false".equals(props.getStringProperty("refresh"));
//						if(refreshmenu!=null)
//							refreshmenu.setState(this.refresh);
//						resetCrawler();
						
						// Load the filter settings
						Properties	filterprops	= props.getSubproperty("mic");
						if(mic instanceof IPropertiesProvider)
							((IPropertiesProvider)mic).setProperties(filterprops)
							.addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result) 
							{
								ret.setResult(null);
							};
						});
						else
						{
							ret.setResult(null);
						}
						
//						if(filterprops!=null)
//						{
//							Property[] mps = filterprops.getProperties();
//							Set selected = new HashSet();
//							for(int i=0; i<mps.length; i++)
//							{
//								if(Boolean.parseBoolean(mps[i].getValue())) 
//									selected.add(mps[i].getType());
//							}
//							filtercon.setSelectedComponentTypes(selected);
//						}
					}
					catch(Exception e)
					{
						ret.setException(e);
						System.err.println("Cannot load project tree: "+e.getClass().getName());
//						e.printStackTrace();
					}
				}
			}	
		});
		
		return ret;
	}
	
	/**
	 *  Get selected file paths.
	 */
	public String[] getSelectionPaths()
	{
		String[] ret = null;
		TreePath[] paths = tree.getSelectionPaths();
		if(paths!=null)
		{
			ret = new String[paths.length];
			if(remote)
			{
				for(int i=0; i<paths.length; i++)
				{
					FileData file = ((RemoteFileNode)paths[i].getLastPathComponent()).getRemoteFile();
					ret[i] = file.getPath();
				}
			}
			else
			{
				for(int i=0; i<paths.length; i++)
				{
					File file = ((FileNode)paths[i].getLastPathComponent()).getFile();
					ret[i] = file.getPath();
				}
			}
		}
		else
		{
			ret = new String[0];
		}
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		JFrame f =  new JFrame();
		FileTreePanel ftp = new FileTreePanel(null);
		DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(new String[]{".doc"}, ftp.getModel());
		ftp.setPopupBuilder(new PopupBuilder(new Object[]{mic}));
		DefaultFileFilter ff = new DefaultFileFilter(mic);
		ftp.setFileFilter(ff);
		ftp.addNodeHandler(new DefaultNodeHandler(ftp.getTree()));
		ftp.addTopLevelNode(new File("c:/"));
		f.add(new JScrollPane(ftp), BorderLayout.CENTER);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
	
	/**
	 *  Delegation filter class.
	 */
	public static class DelegationFilter implements IRemoteFilter
	{
		//-------- attributes --------
		
		/** The delegation filter. */
		protected IRemoteFilter filter;

		//-------- methods --------

		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public IFuture filter(Object obj)
		{
			return filter!=null? filter.filter(obj): new Future(Boolean.TRUE);
		}
		
		/**
		 *  Get the filter.
		 *  @return the filter.
		 */
		public IRemoteFilter getFilter()
		{
			return filter;
		}

		/**
		 *  Set the filter.
		 *  @param filter The filter to set.
		 */
		public void setFilter(IRemoteFilter filter)
		{
			this.filter = filter;
		}
	}
	
	/**
	 *  The delegation icon cache.
	 */
	public static class DelegationIconCache implements IIconCache
	{
		//-------- attributes --------
		
		/** The delegation icon cache. */
		protected IIconCache iconcache;
		
		//-------- methods --------

		/**
		 *  Create a new delegation cache. 
		 */
		public DelegationIconCache()
		{
			this.iconcache = new DefaultIconCache();
		}
		
		/**
		 *  Get an icon.
		 */
		public Icon getIcon(ITreeNode node)
		{
			return iconcache!=null? iconcache.getIcon(node): null;
		}

		/**
		 *  Get the iconcache.
		 *  @return the iconcache.
		 */
		public IIconCache getIconCache()
		{
			return iconcache;
		}

		/**
		 *  Set the iconcache.
		 *  @param iconcache The iconcache to set.
		 */
		public void setIconCache(IIconCache iconcache)
		{
			this.iconcache = iconcache;
		}
	}
}
