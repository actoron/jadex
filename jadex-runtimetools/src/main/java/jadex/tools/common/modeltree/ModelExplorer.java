package jadex.tools.common.modeltree;

import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.bridge.ILibraryService;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.TreeExpansionHandler;
import jadex.tools.common.PopupBuilder;
import jadex.tools.common.SwingWorker;
import jadex.tools.common.ToolTipAction;
import jadex.tools.common.plugin.IControlCenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nuggets.Nuggets;

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
		"checking_menu",	SGUI.makeIcon(ModelExplorer.class, "/jadex/tools/common/images/new_agent_broken.png"),
	});

	//-------- attributes --------

	/** The JCC. */
	protected IControlCenter jcc;

	/** The root node. */
	protected RootNode root;

//	/** The current classloader of the project. */
//	protected ClassLoader	classloader;

	/** Popup rightclick. */
	protected PopupBuilder pubuilder;

	/** The file chooser. */
	protected JFileChooser filechooser;

	/** Tree expansion handler remembers open tree nodes. */
	protected TreeHandler	expansionhandler;
	
	/** The automatic refresh flag. */
	protected boolean	refresh;

	/** The component (icon) to display in the statusbar while refreshing. */
	protected JComponent	refreshcomp;

	/** The refresh menu. */
	protected JCheckBoxMenuItem	refreshmenu;

	/** The check menu. */
	protected JCheckBoxMenuItem	checkingmenu;
	
	/** The selected tree path. */
	protected TreePath selected;
	
	/** The filter names. */
	protected String[] filternames;
	
	/** The possible file filters. */
	protected java.io.FileFilter[] filters;
	
	/** The filter menu. */
	protected JMenu filtermenu;
	
	//-------- constructors --------

	/**
	 *  Create a new ModelExplorer.
	 */
	public ModelExplorer(IControlCenter jcc, RootNode root, JComponent refreshcomp)
	{
		this(jcc, root, refreshcomp, null, null, null);
	}
	
	/**
	 *  Create a new ModelExplorer.
	 *  @param jcc The control center.
	 *  @param root The root node.
	 *  @param refreshcomp The status bar component.
	 *  @param pubuilder The popup builder.
	 *  @param filternames The file filter names.
	 *  @param filters The file filters.
	 */
	public ModelExplorer(IControlCenter jcc, RootNode root, 
		JComponent refreshcomp, PopupBuilder pubuilder, 
		String[] filternames, java.io.FileFilter[] filters)
	{
		super(root);
		this.jcc = jcc;
		this.root = (RootNode)getModel().getRoot();
		root.setMyAgentFactory(jcc.getAgent().getPlatform().getAgentFactory());
		this.refreshcomp	= refreshcomp;
		setRootVisible(false);
		this.refresh	= true;
		this.pubuilder = pubuilder!=null? pubuilder: new PopupBuilder(
			new Action[]{ADD_PATH, REMOVE_PATH, REFRESH, REFRESH_ALL});
		this.filternames = filternames;
		this.filters = filters;
		
		setCellRenderer(new ModelTreeCellRenderer());
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
	 *  Set a nodetype specific action.
	 *  @param nodetype The nodetype.
	 *  @param action The action. 
	 */
	public void setAction(Class nodetype, INodeAction action)
	{
		getRootNode().setAction(nodetype, action);
	}
	
	/**
	 *  Set the file filter.
	 *  @param filter The filter.
	 */
	public void setFileFilter(java.io.FileFilter filter)
	{
		getRootNode().setNewFileFilter(filter);
		refreshAll(null);
	}
	
	/**
	 *  Uncheck all nodes and assign the crawler task
	 *  to refresh all child nodes from start node.
	 *  @param node The start node.
	 */
	public void refreshAll(IExplorerTreeNode node)
	{
		if(node==null)
			node = getRootNode();
		List unchecknodes	= new LinkedList();
		unchecknodes.add(node);
		while(unchecknodes.size()>0)
		{
			node = (IExplorerTreeNode)unchecknodes.remove(0);
			node.uncheck();
			//System.out.println("Uncheck of"+node);
			
			for(int i=0; i<node.getChildCount(); i++)
				unchecknodes.add(node.getChildAt(i));
		}

		refresh(node);
	}
	
	/**
	 *  Set the file filters.
	 *  @param filter The filters.
	 * /
	public void setFileFilters(String[] filternames, java.io.FileFilter[] filters)
	{
		this.filternames = filternames;
		this.filters = filters;
	}*/
	
	/**
	 *  Get the file filters.
	 *  @return The filters.
	 */
	public java.io.FileFilter[] getFileFilters()
	{
		return filters;
	}
	
	/**
	 * @return the root node
	 */
	public RootNode	getRootNode()
	{
		return root;
	}

	
//	/**
//	 * Get the classloader
//	 */
//	public ClassLoader	getClassLoader()
//	{
//		return classloader;
//	}

	/**
	 *  Write current state into properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		// Save root node.
		ClassLoader cl = ((ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
		String	treesave	= Nuggets.objectToXML(getRootNode(), cl);
		props.addProperty(new Property("rootnode", treesave));
				
		// Save the last loaded file.
		File sf = filechooser.getSelectedFile();
		if(sf!=null)
		{
			props.addProperty(new Property("lastpath", sf.getAbsolutePath()));
		}

		// Save the expanded tree nodes.
		int expi=0;
		Enumeration exp = getExpandedDescendants(new TreePath(getRootNode()));
		if(exp!=null)
		{
			while(exp.hasMoreElements())
			{
				TreePath	path	= (TreePath)exp.nextElement();
				if(path.getLastPathComponent() instanceof FileNode)
				{
					props.addProperty(new Property("expanded", ((FileNode)path.getLastPathComponent()).getFile().getAbsolutePath()));
				}
			}
		}
		// Save the selected tree node
		if(getSelectionPath()!=null)
			props.addProperty(new Property("selected", ""+((FileNode)getSelectionPath().getLastPathComponent()).getFile().getAbsolutePath()));

		// Save refresh/checking flags.
		props.addProperty(new Property("refresh", Boolean.toString(refresh)));
		
		// Save the current filter name
		if(filternames!=null && filternames.length>0)
		{
			String name = null;
			for(int i=0; i<filters.length; i++)
			{
				if(filters[i].equals(getRootNode().getFileFilter()))
					name = filternames[i];
			}
			if(name!=null)
			props.addProperty(new Property("filter", name));
		}
		
		return props;
	}

	/**
	 *  Update tool from given properties.
	 */
	public void setProperties(final Properties props)
	{
		// Load root node.
		String	rootxml	= props.getStringProperty("rootnode");
		if(rootxml!=null)
		{
			try
			{
				ClassLoader cl = ((ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
				RootNode newroot = (RootNode)Nuggets.objectFromXML(rootxml, cl);
				newroot.copyFrom(this.root);
				newroot.setMyAgentFactory(jcc.getAgent().getPlatform().getAgentFactory());
				this.root = newroot;
				((DefaultTreeModel)getModel()).setRoot(this.root);
			}
			catch(Exception e)
			{
				System.err.println("Cannot load project tree: "+e.getClass().getName());
				//e.printStackTrace();
			}
		}
		
//		updateClassLoader();
		if(getRootNode().getChildCount()>0)
		{
//			List urls	= new ArrayList();
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
					ILibraryService ls = (ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class);
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
//			classloader	= new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]));
		}
		
		// Load the expanded tree nodes.
		Property[]	expanded	= props.getProperties("expanded");
		for(int i=0; i<expanded.length; i++)
			expansionhandler.treeExpanded(new TreeExpansionEvent(
				this, new TreePath(new FileNode(null, new File(expanded[i].getValue()), null))));

		// Select the last selected model in the tree.
		String sel = props.getStringProperty("selected");
		expansionhandler.setSelectedNode(sel==null ? null : new FileNode(null, new File(sel), null));
		((DefaultTreeModel)getModel()).reload(getRootNode());

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
		if(checkingmenu!=null)
			checkingmenu.setState(getRootNode().isChecking());
		if(refresh)
		{
			if(crawlertask==null)
				crawlertask	=	new CrawlerTask();
			SwingWorker.addTask(crawlertask, PERCENTAGE_CRAWLER);
		}
		
		// Load the current filter name
		if(filternames!=null && filternames.length>0 && filtermenu!=null)
		{
			String name = props.getStringProperty("filter");
			for(int i=0; name!=null && i<filternames.length; i++)
			{
				if(filternames[i].equals(name))
				{
					((JRadioButtonMenuItem)filtermenu.getMenuComponent(i)).setSelected(true);
					setFileFilter(filters[i]);
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
	 *  Update the class loader, when the project settings change.
	 * /
	protected void updateClassLoader()
	{
		if(getRootNode().getChildCount()>0)
		{
			List urls	= new ArrayList();
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
				try
				{
					urls.add(file.toURL());
				}
				catch(MalformedURLException ex)
				{
					String failed = SUtil.wrapText("Could not add path\n\n"+ex.getMessage());
					JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelExplorer.this), failed, "Path Error", JOptionPane.ERROR_MESSAGE);
					//e.printStackTrace();
				}
			}
//			classloader	= new URLClassLoader((URL[])urls.toArray(new URL[urls.size()]));
		}
//		else
//		{
//			classloader	= null;
//		}
	}*/
	
	/**
	 *  Reset the tree.
	 */
	public void reset()
	{
		refresh	= false;	// stops crawler task, if any
		// Stop user task (hack!!!?)
		if(usertask!=null)
		{
			usertask.nodes_user.clear();
			usertask.nodes_out.clear();
		}		

		root.reset();
		((DefaultTreeModel)getModel()).nodeStructureChanged(root);
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

	/**
	 *  Recursively check models starting with the given node.
	 *  @param node	The starting point.
	 *  @param baseurl	The baseurl to load models relative to parent node.
	 *  @return true if
	 */
	protected boolean	check(TreeNode node, String baseurl)
	{
		baseurl	= baseurl==null ? node.toString() : baseurl+ "/" + node.toString(); 
		boolean	ok	= true;	// True, when all files ok
		IJadexAgentFactory fac = jcc.getAgent().getPlatform().getAgentFactory();
		
		// Has children, must be directory.
		if(node.getChildCount()>0)
		{
			for(int i=0; i<node.getChildCount(); i++)
			{
				boolean	check	= check(node.getChildAt(i), baseurl);
				ok	= ok && check;
			}
		}
		
		// No more children, could be model
		// todo: other kernel extensions
		else if(fac.isLoadable(baseurl))
		{
			try
			{
				IJadexModel model = fac.loadModel(baseurl);
				if(model!=null)
				{
					ok	= !checkingmenu.isSelected() || model.getReport().isEmpty();
				}
				// else unknown jadex file type -> ignore.
			}
			catch(Exception e)
			{
				ok	= false;
			}
		}

		// Add check result to lookup table (used by tree cell renderer).
//		checkstate.put(node, broken ? CHECK_BROKEN : ok ? CHECK_OK : CHECK_PARTIAL);
		
		return ok;
	}
	
	/**
	 *  Create the menu bar.
	 *  @return the same modified menubar
	 */
	public JMenu[] createMenuBar()
	{
		JMenu	menu	= new JMenu("Model");
		
		this.refreshmenu = new JCheckBoxMenuItem(TOGGLE_REFRESH);
		refreshmenu.setState(this.refresh);
		menu.add(refreshmenu);
		
		this.checkingmenu = new JCheckBoxMenuItem(TOGGLE_CHECKING);
		checkingmenu.setState(getRootNode().isChecking());
		menu.add(checkingmenu);
		
		//menubar.add(menu, menubar.getComponentCount()-1);
		
		if(filters!=null && filters.length>1)
		{
			filtermenu = new JMenu("File filter");
			ButtonGroup bg = new ButtonGroup();
			for(int i=0; i<filters.length; i++)
			{
				final java.io.FileFilter filter = filters[i];
				JRadioButtonMenuItem ff = new JRadioButtonMenuItem(""+filternames[i]);
				bg.add(ff);
				filtermenu.add(ff);
				ff.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(!getRootNode().getFileFilter().equals(filter))
						{
							setFileFilter(filter);
							((DefaultTreeModel)getModel()).reload(getRootNode());
						}
					}
				});
				if(getRootNode().getFileFilter().equals(filter))
					ff.setSelected(true);
			}
			menu.add(filtermenu);
		}
		
		return new JMenu[]{menu};
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
	
	/** The high priority background task. */
	protected UserTask	usertask;

	/** The low priority background task. */
	protected CrawlerTask	crawlertask;

	/**
	 *  Refresh a node.
	 */
	public void	refresh(IExplorerTreeNode node)
	{
		if(usertask==null)
		{
			usertask	= new UserTask();
			SwingWorker.addTask(usertask, PERCENTAGE_USER);
			if(refreshcomp!=null)
				jcc.addStatusComponent(this, refreshcomp);
		}
		usertask.nodes_user.add(node);
	}

	/**
	 *  The user-level refresher task.
	 */
	public class UserTask	implements SwingWorker.Task
	{
		/** User nodes to be refreshed (including children). */
		protected List	nodes_user	= new LinkedList();
	
		/** Changed nodes to be refreshed (including parents). */
		protected List	nodes_out	= new LinkedList();

		/**
		 * 
		 */
		public boolean execute()
		{
//			System.out.println("refresher: "+nodes_user+", "+nodes_out);

			// Process user nodes (iterate through children).
			if(!nodes_user.isEmpty())
			{
				// Update node, if necessary.
				final IExplorerTreeNode	node	= (IExplorerTreeNode)nodes_user.remove(0);
				String	tip	= node.getToolTipText();
				if(tip!=null)
					jcc.setStatusText("Scanning "+tip);
				if(node.refresh())
				{
//					System.out.println("change detected in user node: "+node);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((DefaultTreeModel)getModel()).reload(node);
						}
					});
					if(!nodes_out.contains(node))
						nodes_out.add(0, node);	// Add to start for inner nodes being processed first (inverse order)
				}
				
				// For user nodes iterate over children, regardless if node has changed.
				Enumeration	children	= node.children();
				while(children.hasMoreElements())
				{
					nodes_user.add(children.nextElement());
				}
			}
	
			// Process out nodes (traverse back to root).
			else if(!nodes_out.isEmpty())
			{
				// Update node, as often as necessary (e.g. integrity checks are performed on 2nd refresh).
				final IExplorerTreeNode	node	= (IExplorerTreeNode)nodes_out.remove(0);
				String	tip	= node.getToolTipText();
				if(tip!=null)
					jcc.setStatusText("Scanning "+tip);
				if(node.refresh())
				{
//					System.out.println("change detected in out node: "+node);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((DefaultTreeModel)getModel()).reload(node);
						}
					});
					nodes_out.add(node);
				}
				
				// Otherwise move to parent node
				else if(node.getParent()!=null)
				{
					if(!nodes_out.contains(node.getParent()))
						nodes_out.add(node.getParent());
				}
			}
			
			boolean finished	= nodes_user.isEmpty() && nodes_out.isEmpty();
			if(finished)
			{
				usertask	= null;
				if(refreshcomp!=null)
					jcc.removeStatusComponent(ModelExplorer.this);
				if(refresh)
				{
					if(crawlertask==null)
						crawlertask	=	new CrawlerTask();
					SwingWorker.addTask(crawlertask, PERCENTAGE_CRAWLER);
				}
			}

			return !finished;
		}
	}

	/**
	 *  The crawler-level refresher task.
	 */
	public class CrawlerTask	implements SwingWorker.Task
	{
		/** Crawler nodes to be refreshed (including children). */
		protected List	nodes_crawler	= new ArrayList();

		public boolean execute()
		{
//			ClassLoader	oldcl	= Thread.currentThread().getContextClassLoader();
//			if(classloader!=null)
//				Thread.currentThread().setContextClassLoader(classloader);

			if(nodes_crawler.isEmpty())
			{
				nodes_crawler.add(getRootNode());
//				System.out.println("restarted: "+this);
			}

			// Update node if necessary:
			final IExplorerTreeNode	node	= (IExplorerTreeNode)nodes_crawler.remove(0);
			if(node.refresh())
			{
//				System.out.println("change detected in crawler node: "+node);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						((DefaultTreeModel)getModel()).reload(node);
					}
				});
				
				// Scan changed node and subnodes with user priority.
				refresh(node);
			}
			
			// Iterate over children:
			Enumeration	children	= node.children();
			while(children.hasMoreElements())
			{
				nodes_crawler.add(children.nextElement());
			}

//			Thread.currentThread().setContextClassLoader(oldcl);

			return refresh && usertask==null;
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
			if(refresh)
			{
				if(crawlertask==null)
					crawlertask	=	new CrawlerTask();
				SwingWorker.addTask(crawlertask, PERCENTAGE_CRAWLER);
			}
		}
	};

	/**
	 *  The action for changing integrity checking settings.
	 */
	public final AbstractAction TOGGLE_CHECKING = new AbstractAction("Auto check", icons.getIcon("checking_menu"))
	{
		public void actionPerformed(ActionEvent e)
		{
			getRootNode().setChecking(((JCheckBoxMenuItem)e.getSource()).getState());
			refresh(getRootNode());
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
						//updateClassLoader();
						
						// todo: jars
						ILibraryService ls = (ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class);
						file = new File(file.getParentFile(), file.getName());
						try
						{
							ls.addURL(file.toURI().toURL());
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
						
						((DefaultTreeModel)getModel()).reload(getRootNode());
						refresh(node);
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
			TreeNode rm = (TreeNode)getLastSelectedPathComponent();
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
				getRootNode().removePathEntry(node);
				
				//updateClassLoader();
				// todo: jars
				ILibraryService ls = (ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class);
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
				
				// Stop user task (hack!!!?)
				if(usertask!=null)
				{
					usertask.nodes_user.clear();
					usertask.nodes_out.clear();
				}
				// Reset crawler task (hack!!!?)
				if(crawlertask!=null)
				{
					crawlertask.nodes_crawler.clear();
				}
				refresh(getRootNode());

				((DefaultTreeModel)getModel()).reload(getRootNode());
			}
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			TreeNode rm = (TreeNode)getLastSelectedPathComponent();
			return rm!=null && rm.getParent()==getRootNode();
		}
	};

	/**
	 *  Refresh the selected path.
	 */
	public final Action REFRESH = new ToolTipAction("Refresh", icons.getIcon("refresh"), null)
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			FileNode	node	= (FileNode)getLastSelectedPathComponent();
			node.uncheck();
			
			// why recursively??? Shouldn't we use refreshAll()?
			refreshAll(node);
			
			/*if(node instanceof DirNode)
				((DirNode)node).refreshAll();
			else
				node.refresh();*/
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			return getLastSelectedPathComponent()!=null && ((FileNode)getLastSelectedPathComponent()).getChildCount()==0;
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
				ret = "Refresh directory recursively: "+((DirNode)tmp).getFile().getName();
			else if(tmp instanceof FileNode)
				ret = "Refresh file: "+((FileNode)tmp).getFile().getName();
			return ret;
		}
	};

	/**
	 *  Refresh all items.
	 */
	public final Action REFRESH_ALL = new ToolTipAction("Refresh All", icons.getIcon("refresh"), "Refresh all items of tree")
	{
		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			return getLastSelectedPathComponent()==null || ((FileNode)getLastSelectedPathComponent()).getChildCount()!=0;
		}

		public void actionPerformed(ActionEvent e)
		{
			IExplorerTreeNode	node	= (IExplorerTreeNode)getLastSelectedPathComponent();
			if(node==null)
			{
				node	= getRootNode();
			}
			
			refreshAll(node);
			
			// Recursively refresh all nodes.
			//((RootNode)node).refreshAll();
			
			/*List	unchecknodes	= new LinkedList();
			unchecknodes.add(node);
			while(unchecknodes.size()>0)
			{
				node	= (IExplorerTreeNode)unchecknodes.remove(0);
				node.uncheck();
				for(int i=0; i<node.getChildCount(); i++)
				{
					unchecknodes.add(node.getChildAt(i));
				}
			}

			refresh(node);*/
		}
	};
}




