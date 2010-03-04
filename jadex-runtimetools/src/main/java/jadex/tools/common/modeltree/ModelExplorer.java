package jadex.tools.common.modeltree;

import jadex.base.SComponentFactory;
import jadex.bridge.IComponentFactory;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.TreeExpansionHandler;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.LoadManagingExecutionService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;
import jadex.service.threadpool.ThreadPoolService;
import jadex.tools.common.PopupBuilder;
import jadex.tools.common.ToolTipAction;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
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
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;


/**
 *  The model panel.
 */
public class ModelExplorer extends JTree
{
	//-------- constants --------

	/** The max time to check nodes as requested by the user (default 90%). */
	public static double	PERCENTAGE_USER	= 0.90;
	
	/** The max time to check in the background (default 2%). */
	public static double	PERCENTAGE_CRAWLER	= 0.02;
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_addfolder.png"),
		"removepath",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_removefolder.png"),
		"checker",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_checker.png"),
		"refresh",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_refresh.png"),
		"refresh_menu",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_refresh_small.png"),
	});

	//-------- attributes --------

	/** The service container. */
	protected IServiceContainer container;

	/** The root node. */
	protected RootNode root;
	
	/** The node functionality. */
	protected DefaultNodeFunctionality	nof;
	
	/** The background work manager. */
	protected LoadManagingExecutionService	worker;
	
	/** The crawler task. */
	protected CrawlerTask	crawlertask;

	/** Popup rightclick. */
	protected PopupBuilder pubuilder;

	/** The file chooser. */
	protected JFileChooser filechooser;

	/** Tree expansion handler remembers open tree nodes. */
	protected TreeHandler	expansionhandler;
	
	/** The automatic refresh flag. */
	protected boolean	refresh;

	/** The refresh menu. */
	protected JCheckBoxMenuItem	refreshmenu;

	/** The selected tree path. */
	protected TreePath selected;
	
	/** The filter menu. */
	protected JMenu filtermenu;
	
	/** The file filter. */
	protected java.io.FileFilter	filefilter;
	
	//-------- constructors --------

	/**
	 *  Create a new ModelExplorer.
	 */
	public ModelExplorer(IServiceContainer container, DefaultNodeFunctionality nof)
	{
		super(new ModelExplorerTreeModel(new RootNode(), nof));
		this.container = container;
		this.nof = nof;
		nof.setModelExplorer(this);
		this.root = (RootNode)getModel().getRoot();
		setRootVisible(false);
		this.refresh	= true;
		this.pubuilder = pubuilder!=null? pubuilder: new PopupBuilder(
			new Action[]{ADD_PATH, REMOVE_PATH, REFRESH});
		this.worker	= new LoadManagingExecutionService(
			((IThreadPool)container.getService(ThreadPoolService.class)));
		
		setCellRenderer(new ModelTreeCellRenderer(nof));
		setRowHeight(16);
		addMouseListener(new MouseAdapter()
		{
			/**
			 * shows popup
			 * @param e The event.
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
					showPopUp(e.getX(), e.getY());
			}

			/**
			 * shows popup
			 * @param e The event.
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
					showPopUp(e.getX(), e.getY());
			}
		});
		//addTreeSelectionListener(this);
		setScrollsOnExpand(true);
		this.expansionhandler	= new TreeHandler(this);

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
		
		getModel().addTreeModelListener(new TreeModelListener()
		{
			public void treeNodesChanged(TreeModelEvent e)
			{
			}

			public void treeNodesInserted(TreeModelEvent e)
			{
			}

			public void treeNodesRemoved(TreeModelEvent e)
			{
			}

			public void treeStructureChanged(TreeModelEvent e)
			{
				if(selected!=null)
				{
					//System.out.println("Selecting: "+selected+" "+e.getTreePath());
					//System.out.println(selected.getLastPathComponent().equals(e.getTreePath().getLastPathComponent()));
					//if(selected.getLastPathComponent().equals(e.getTreePath().getLastPathComponent()))
					expansionhandler.setSelectedNode((FileNode)selected.getLastPathComponent());
					/*{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								expansionhandler.setSelectedNode((FileNode)selected.getLastPathComponent());
							}
						});
					}*/
				}
			}
		});
		
		addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				//System.out.println("Selected: "+e.getPath()+" "+e.getSource());
				selected = e.getPath();
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_DELETE)
				{
					REMOVE_PATH.actionPerformed(null);
				}
			}
		});
		ToolTipManager.sharedInstance().registerComponent(this);
		
		final ILibraryService ls = (ILibraryService)container.getService(ILibraryService.class);
		ls.addLibraryServiceListener(new ILibraryServiceListener()
		{
			public void urlAdded(URL url)
			{
			}
			public void urlRemoved(URL url)
			{
				List cs = getRootNode().getChildren();
				for(int i=0; cs!=null && i<cs.size(); i++)
				{
					try
					{
						FileNode fn = (FileNode)cs.get(i);
						URL furl = fn.getFile().toURI().toURL();
						if(url.equals(furl))
						{
							int	index	= ((ModelExplorerTreeModel)getModel()).getIndexOfChild(root, fn);
							getRootNode().removePathEntry(fn);
							((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), fn, index);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
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
					REFRESH.actionPerformed(null);
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
		});
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
	 *  Recursively refresh a node and its subnodes.
	 */
	public void refreshAll(IExplorerTreeNode node)
	{
		worker.execute(new RecursiveRefreshTask(node), PERCENTAGE_USER);
	}
	
	/**
	 * @return the root node
	 */
	public RootNode	getRootNode()
	{
		return root;
	}

	/**
	 *  Struct for storing Tree proeprties.
	 */
	public static class	ModelExplorerProperties
	{
		/** The root node. */
		protected RootNode	root;
		/** The selected node (if any). */
		protected FileNode	selected;
		/** This list of expanded nodes. */
		protected List	expanded;
		
		public RootNode	getRootNode(){return root;}
		public FileNode	getSelectedNode(){return selected;}
		public List	getExpandedNodes(){return expanded;}
		public void	setRootNode(RootNode root){this.root=root;}
		public void	setSelectedNode(FileNode selected){this.selected=selected;}
		public void	setExpandedNodes(List expanded){this.expanded=expanded;}
	}
	
	/**
	 *  Write current state into properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		// Save tree properties.
		ModelExplorerProperties	mep	= new ModelExplorerProperties();
		mep.root	= getRootNode();
		mep.selected	= getSelectionPath()==null ? null
			: (FileNode)getSelectionPath().getLastPathComponent();
		mep.expanded	= new ArrayList();
		Enumeration exp = getExpandedDescendants(new TreePath(getRootNode()));
		if(exp!=null)
		{
			while(exp.hasMoreElements())
			{
				TreePath	path	= (TreePath)exp.nextElement();
				if(path.getLastPathComponent() instanceof FileNode)
				{
					mep.expanded.add(path.getLastPathComponent());
				}
			}
		}
		ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
		String	treesave	= JavaWriter.objectToXML(mep, cl);	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
//		String	treesave	= Nuggets.objectToXML(mep, cl);
		props.addProperty(new Property("tree", treesave));
				
		// Save the last loaded file.
		File sf = filechooser.getSelectedFile();
		if(sf!=null)
		{
			String	lastpath	= SUtil.convertPathToRelative(sf.getAbsolutePath());
			props.addProperty(new Property("lastpath", lastpath));
		}

		// Save refresh/checking flags.
		props.addProperty(new Property("refresh", Boolean.toString(refresh)));
		
		// Save the state of file filters
		if(filtermenu!=null && filtermenu.getComponentCount()>0)
		{
			Properties	filterprops	= new Properties(null, "filter", null);
			for(int i=0; i<filtermenu.getComponentCount(); i++)
			{
				String	name	= ((JCheckBoxMenuItem)filtermenu.getComponent(i)).getText();
				boolean	selected	= ((JCheckBoxMenuItem)filtermenu.getComponent(i)).isSelected();
				filterprops.addProperty(new Property(name, ""+selected));
			}
			props.addSubproperties(filterprops);
		}
		
		return props;
	}

	/**
	 *  Update tool from given properties.
	 */
	public void setProperties(final Properties props)
	{
		// Load root node.
		String	treexml	= props.getStringProperty("tree");
		if(treexml!=null)
		{
			try
			{
				ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
				ModelExplorerProperties	mep	= (ModelExplorerProperties)JavaReader.objectFromXML(treexml, cl); 	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
//				ModelExplorerProperties	mep	= (ModelExplorerProperties)Nuggets.objectFromXML(treexml, cl);
				this.root	= mep.root;
				((ModelExplorerTreeModel)getModel()).setRoot(this.root);

				// Select the last selected model in the tree.
				expansionhandler.setSelectedNode(mep.selected);

				// Load the expanded tree nodes.
				for(int i=0; i<mep.expanded.size(); i++)
					expansionhandler.treeExpanded(new TreeExpansionEvent(
						this, new TreePath(mep.expanded.get(i))));

				((ModelExplorerTreeModel)getModel()).fireTreeStructureChanged(getRootNode());
			}
			catch(Exception e)
			{
				System.err.println("Cannot load project tree: "+e.getClass().getName());
//				e.printStackTrace();
			}
		}
		
		if(getRootNode().getChildCount()>0)
		{
			for(Enumeration e=getRootNode().children(); e.hasMoreElements();)
			{
				// Todo: support non-file (e.g. url nodes).
				File	file	= ((FileNode)e.nextElement()).getFile();
				
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
//				try
				{
					ILibraryService ls = (ILibraryService)container.getService(ILibraryService.class);
//					ls.addPath(file.getAbsolutePath());
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
//				catch(MalformedURLException ex)
//				{
//					String failed = SUtil.wrapText("Could not add path\n\n"+ex.getMessage());
//					JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelExplorer.this), failed, "Path Error", JOptionPane.ERROR_MESSAGE);
					//e.printStackTrace();
//				}
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
		refresh	= !"false".equals(props.getStringProperty("refresh"));
		if(refreshmenu!=null)
			refreshmenu.setState(this.refresh);
		
		resetCrawler();
		
		// Load the filter settings
		Properties	filterprops	= props.getSubproperty("filter");
		if(filterprops!=null && filtermenu!=null && filtermenu.getComponentCount()>0)
		{
			for(int i=0; i<filtermenu.getComponentCount(); i++)
			{
				JCheckBoxMenuItem	item	= (JCheckBoxMenuItem)filtermenu.getComponent(i);
				String	name	= item.getText();
				if(filterprops.getProperty(name)!=null)
				{
					item.setSelected(filterprops.getBooleanProperty(name));
				}
				else
				{
					item.setSelected(true);
				}
			}
		}
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				refresh(getRootNode());
//			}
//		});
	}
	
	/**
	 *  Reset the tree.
	 */
	public void reset()
	{
		refresh	= false;	// stops crawler task, if any
//		// Stop user task (hack!!!?)
//		if(usertask!=null)
//		{
//			usertask.nodes_user.clear();
//			usertask.nodes_out.clear();
//		}		

		// Remove libraries.
		Object[] cs = getRootNode().getChildCount()>0 ? getRootNode().getChildren().toArray() : null;
		if(cs!=null)
		{
			ILibraryService ls = (ILibraryService)container.getService(ILibraryService.class);
			for(int i=0; i<cs.length; i++)
			{
				try
				{
					FileNode fn = (FileNode)cs[i];
					ls.removeURL(fn.getFile().toURI().toURL());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		root.reset();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				refreshmenu.setSelected(refresh);
				((ModelExplorerTreeModel)getModel()).fireTreeStructureChanged(root);
			}
		});
	}
	
	/**
	 *  Reset the crawler (e.g. when directories have been removed)
	 */
	public void	resetCrawler()
	{
		if(crawlertask!=null)
		{
			crawlertask.abort();
			crawlertask	= null;
		}
		
		if(refresh)
		{
			crawlertask	=	new CrawlerTask();
			worker.execute(crawlertask, PERCENTAGE_CRAWLER);
		}
	}
	
	/**
	 *  Called when the agent is closed.
	 */
	public void	close()
	{
	}

	/**
	 * Show the popup.
	 * @param x The x position.
	 * @param y The y position.
	 */
	protected void showPopUp(int x, int y)
	{
		TreePath sel = getPathForLocation(x, y);
		setSelectionPath(sel);

		JPopupMenu pop = pubuilder.buildPopupMenu();
		pop.show(this, x, y);
	}

//	/**
//	 *  Recursively check models starting with the given node.
//	 *  @param node	The starting point.
//	 *  @param baseurl	The baseurl to load models relative to parent node.
//	 *  @return true if
//	 */
//	protected boolean	check(TreeNode node, String baseurl)
//	{
//		baseurl	= baseurl==null ? node.toString() : baseurl+ "/" + node.toString(); 
//		boolean	ok	= true;	// True, when all files ok
//		IJadexAgentFactory fac = jcc.getAgent().getPlatform().getAgentFactory();
//		
//		// Has children, must be directory.
//		if(node.getChildCount()>0)
//		{
//			for(int i=0; i<node.getChildCount(); i++)
//			{
//				boolean	check	= check(node.getChildAt(i), baseurl);
//				ok	= ok && check;
//			}
//		}
//		
//		// No more children, could be model
//		// todo: other kernel extensions
//		else if(fac.isLoadable(baseurl))
//		{
//			try
//			{
//				IJadexModel model = fac.loadModel(baseurl);
//				if(model!=null)
//				{
//					ok	= !checkingmenu.isSelected() || model.getReport().isEmpty();
//				}
//				// else unknown jadex file type -> ignore.
//			}
//			catch(Exception e)
//			{
//				ok	= false;
//			}
//		}
//
//		// Add check result to lookup table (used by tree cell renderer).
////		checkstate.put(node, broken ? CHECK_BROKEN : ok ? CHECK_OK : CHECK_PARTIAL);
//		
//		return ok;
//	}
	
	/**
	 *  Create the menu items.
	 */
	public JMenu[] createMenuBar()
	{
		JMenu	menu	= new JMenu("Model");
		
		this.refreshmenu = new JCheckBoxMenuItem(TOGGLE_REFRESH);
		refreshmenu.setState(this.refresh);
		menu.add(refreshmenu);
		
//		String[] ft1 = jcc.getAgent().getPlatform().getAgentFactory().getFileTypes();
//		String[] ft2 = jcc.getAgent().getPlatform().getApplicationFactory().getFileTypes();
//		String[] filetypes = (String[])SUtil.joinArrays(ft1, ft2);
//		
		
		Collection facts = container.getServices(IComponentFactory.class);
		
		if(facts!=null)
		{
//			if(filetypes.length>1)
//			{
//				Icon[]	icons	= new Icon[filetypes.length];
//				for(int i=0; i<ft1.length; i++)
//				{
//					icons[i] = jcc.getAgent().getPlatform().getAgentFactory().getFileTypeIcon(ft1[i]);
//				}
//				for(int i=0; i<ft2.length; i++)
//				{
//					icons[ft1.length+i]	= jcc.getAgent().getPlatform().getApplicationFactory().getFileTypeIcon(ft2[i]);
//				}
	
				filtermenu = new JMenu("File filter");

//				for(int i=0; i<filetypes.length; i++)
				for(Iterator it=facts.iterator(); it.hasNext(); )
				{
					IComponentFactory fac = (IComponentFactory)it.next();
					
					String[] filetypes = fac.getComponentTypes();
					for(int i=0; i<filetypes.length; i++)
					{
						JCheckBoxMenuItem ff = new JCheckBoxMenuItem(filetypes[i], (Icon)fac.getComponentTypeIcon(filetypes[i]), true);
						filtermenu.add(ff);
						ff.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								refreshAll(getRootNode());
							}
						});
					}
				}
				menu.add(filtermenu);
//			}
		}
		return new JMenu[]{menu};
	}

	/**
	 *	Get a file filter according to current file type settings. 
	 */
	public java.io.FileFilter getFileFilter()
	{
		if(filefilter==null)
		{
			synchronized(this)
			{
				if(filefilter==null)
				{
					filefilter	= new java.io.FileFilter()
					{
						public boolean accept(File file)
						{
							return file.isDirectory() 
								|| SComponentFactory.isLoadable(container, file.getAbsolutePath());
						}
					};
				}
			}
		}
		return filefilter;
	}

	//-------- helper classes --------
	
	/**
	 *  Handles all aspects (e.g. expansion, selection) of the tree
	 *  that have to happen in background
	 *  as the refresher thread adds/removes nodes.
	 */
	public static class TreeHandler extends TreeExpansionHandler
	{
		//-------- attributes --------
		
		/** The node that was selected before the current project was last saved. */
		// Hack!!! Move to treeselection listener.
		protected FileNode	lastselected;

		//-------- constructors --------
		
		/**
		 *  Create a new tree handler.
		 */
		public TreeHandler(JTree tree)
		{
			super(tree);
		}
		
		//-------- methods --------

		/**
		 *  Set the selected node.
		 */
		public void	setSelectedNode(FileNode node)
		{
			this.lastselected	= node;
		}
	
		/**
		 *  Check if an action (e.g. expand) has to be performed on the path.
		 */
		protected void handlePath(TreePath path)
		{
			super.handlePath(path);
		
			// Check if the node that was saved as selected is added.
			if(lastselected!=null && lastselected.equals(path.getLastPathComponent()))
			{
				lastselected	= null;
				tree.setSelectionPath(path);
			}
		}
	}

	//-------- tree refreshing --------
	
	/**
	 *  Get the background work manager.
	 */
	public LoadManagingExecutionService	getWorker()
	{
		return this.worker;
	}

	/**
	 *  A task to recursively refresh a node and its children.
	 */
	class RecursiveRefreshTask	implements IExecutable
	{
		//-------- attributes --------
		
		/** The node to refresh. */
		protected IExplorerTreeNode	node;
		
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public RecursiveRefreshTask(IExplorerTreeNode node)
		{
			this.node	= node;
		}
		
		//-------- IExecutable interface --------
		
		/**
		 *  Execute the task.
		 */
		public boolean execute()
		{
			if(nof.isValidChild(node))
			{
				nof.startNodeTask(new DefaultNodeFunctionality.RefreshNodeTask(nof, node));
				int	children	= ((ModelExplorerTreeModel)getModel()).getChildCount(node);
				for(int i=0; i<children; i++)
				{
					IExplorerTreeNode	child	= (IExplorerTreeNode) ((ModelExplorerTreeModel)getModel()).getChild(node, i);
					worker.execute(new RecursiveRefreshTask(child), PERCENTAGE_USER);
				}
			}
			return false;
		}
	}
	
	/**
	 *  The crawler-level refresher task.
	 */
	public class CrawlerTask	implements IExecutable
	{
		/** Crawler nodes to be refreshed (including children). */
		protected List	nodes_crawler	= new ArrayList();
		
		/** Abort flag to exit this crawler task (e.g. when project changed). */
		protected boolean	abort;

		public boolean execute()
		{
			if(abort)
				return false;
			
			if(nodes_crawler.isEmpty())
			{
				nodes_crawler.add(getRootNode());
//				System.out.println("restarted: "+this);
			}

			// Update node if necessary:
			final IExplorerTreeNode	node	= (IExplorerTreeNode)nodes_crawler.remove(0);
//			SwingUtilities.invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					jcc.setStatusText("Crawling "+node.getToolTipText());
//				}
//			});
			nof.refresh(node);	// Refresh in crawler and do not start task on user priority
			
			// Iterate over children
			int	children	= ((ModelExplorerTreeModel)getModel()).getChildCount(node);
			for(int i=0; i<children; i++)
			{
				IExplorerTreeNode	child	= (IExplorerTreeNode) ((ModelExplorerTreeModel)getModel()).getChild(node, i);
				nodes_crawler.add(child);
			}

			return refresh;
		}
		
		/**
		 *  Abort this crawler task (e.g. when project has changed).
		 */
		public void	abort()
		{
			this.abort	= true;
		}
	}
	
	/**
	 *  The action for changing refresh settings.
	 */
	public final AbstractAction TOGGLE_REFRESH = new AbstractAction("Auto refresh", icons.getIcon("refresh_menu"))
	{
		public void actionPerformed(ActionEvent e)
		{
			refresh	= ((JCheckBoxMenuItem)e.getSource()).getState();
			resetCrawler();
		}
	};
	
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
			if(filechooser.showDialog(SGUI.getWindowParent(ModelExplorer.this)
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
						IExplorerTreeNode	node	= getRootNode().addPathEntry(file);
						
						// todo: jars
						ILibraryService ls = (ILibraryService)container.getService(ILibraryService.class);
						file = new File(file.getParentFile(), file.getName());
						try
						{
							ls.addURL(file.toURI().toURL());
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
						
						((ModelExplorerTreeModel)getModel()).fireNodeAdded(getRootNode(), node,
							((ModelExplorerTreeModel)getModel()).getChildCount(root)-1);
					}
					else
					{
						String	msg	= SUtil.wrapText("Cannot find file or directory:\n"+file);
						JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelExplorer.this),
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
			IExplorerTreeNode rm = (IExplorerTreeNode)getLastSelectedPathComponent();
			return rm==null;
		}
	};

	/**
	 *  Remove an existing path from the explorer.
	 */
	public final Action REMOVE_PATH = new ToolTipAction("Remove Path", icons.getIcon("removepath"),
		"Remove a directory path to the project structure")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(isEnabled())
			{
				FileNode	node	= (FileNode)getLastSelectedPathComponent();
				int index	= ((ModelExplorerTreeModel)getModel()).getIndexOfChild(root, node);
				getRootNode().removePathEntry(node);
				
				// todo: jars
				ILibraryService ls = (ILibraryService)container.getService(ILibraryService.class);
				File file = node.getFile();
				file = new File(file.getParentFile(), file.getName());
				try
				{
					ls.removeURL(file.toURI().toURL());
				}
				catch(MalformedURLException ex)
				{
					ex.printStackTrace();
				}
				
				resetCrawler();

				((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), node, index);
			}
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IExplorerTreeNode rm = (IExplorerTreeNode)getLastSelectedPathComponent();
			return rm!=null && rm.getParent()==getRootNode();
		}
	};

	/**
	 *  Refresh the selected path.
	 */
	public final Action REFRESH = new ToolTipAction("Refresh [F5]", icons.getIcon("refresh"), null)
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			IExplorerTreeNode	node	= (IExplorerTreeNode)getLastSelectedPathComponent();
			refreshAll(node!=null ? node : getRootNode());
		}

		/**
		 * Get the tool tip text.
		 * @return The tool tip text.
		 */
		public String getToolTipText()
		{
			String ret = null;
			Object tmp = getLastSelectedPathComponent();
			if(tmp instanceof DirNode)
				ret = "Refresh directory recursively [F5]: "+((DirNode)tmp).getFile().getName();
			else if(tmp instanceof FileNode)
				ret = "Refresh file [F5]: "+((FileNode)tmp).getFile().getName();
			else
				ret = "Refresh all items of tree [F5]";
			return ret;
		}
	};
}





